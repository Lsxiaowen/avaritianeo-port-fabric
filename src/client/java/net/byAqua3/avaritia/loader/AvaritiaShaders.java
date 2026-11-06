package net.byAqua3.avaritia.loader;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.WeakHashMap;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import net.byAqua3.avaritia.Avaritia;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicUniformStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;

public final class AvaritiaShaders {
	private static final Map<RenderType, Parameters> PARAMETERS = new WeakHashMap<>();
	private static DynamicUniformStorage<Uniform> uniforms;

	private AvaritiaShaders() {
	}

	public record Parameters(float time, float yaw, float pitch, float scale, float opacity) {
	}

	private record Uniform(Parameters parameters, float[] uvs) implements DynamicUniformStorage.DynamicUniform {
		@Override
		public void write(ByteBuffer buffer) {
			boolean inventory = RenderSystem.getProjectionType() == ProjectionType.ORTHOGRAPHIC;
			float scale = inventory ? 100.0F : parameters.scale();
			buffer.putFloat(parameters.time()).putFloat(inventory ? 0 : parameters.yaw())
					.putFloat(inventory ? 0 : parameters.pitch()).putFloat(scale);
			buffer.putFloat(parameters.opacity()).putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
			for (float value : uvs) {
				buffer.putFloat(value);
			}
		}
	}

	public static float time() {
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft.level == null ? 0.0F : minecraft.level.getGameTime() % Integer.MAX_VALUE;
	}

	static void register(RenderType type, Parameters parameters) {
		PARAMETERS.put(type, parameters);
	}

	public static GpuBufferSlice prepare(RenderType type) {
		Parameters parameters = PARAMETERS.get(type);
		if (parameters == null) {
			return null;
		}
		if (uniforms == null) {
			uniforms = new DynamicUniformStorage<>("Avaritia cosmic parameters", 192, 256);
		}
		TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
		float[] uvs = new float[40];
		for (int index = 0; index < 10; index++) {
			TextureAtlasSprite sprite = atlas.getSprite(Avaritia.id("shader/cosmic_" + index));
			uvs[index * 4] = sprite.getU0();
			uvs[index * 4 + 1] = sprite.getV0();
			uvs[index * 4 + 2] = sprite.getU1();
			uvs[index * 4 + 3] = sprite.getV1();
		}
		return uniforms.writeUniform(new Uniform(parameters, uvs));
	}

	public static void bind(GpuBufferSlice parameters, RenderPass pass) {
		if (parameters != null) {
			pass.setUniform("CosmicParameters", parameters);
		}
	}

	public static void endFrame() {
		AvaritiaRenderTypes.endFrame();
		if (uniforms != null) {
			uniforms.endFrame();
		}
	}

	public static void close() {
		if (uniforms != null) {
			uniforms.close();
			uniforms = null;
		}
		PARAMETERS.clear();
		AvaritiaRenderTypes.close();
	}
}
