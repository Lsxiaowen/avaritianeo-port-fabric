package net.byAqua3.avaritia.render.item;

import java.util.List;
import java.util.function.Consumer;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.loader.AvaritiaRenderTypes;
import net.byAqua3.avaritia.loader.AvaritiaShaders;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class ItemEffectsRenderer implements SpecialModelRenderer<ItemEffectsRenderer.Arguments> {
	private static final Identifier HALO = Avaritia.id("item/halo");
	private static final Identifier HALO_NOISE = Avaritia.id("item/halo_noise");
	private final List<BakedQuad> quads;
	private final Vector3fc[] extents;
	private final int haloType;
	private final float alpha;

	public ItemEffectsRenderer(List<BakedQuad> quads, int haloType, float alpha) {
		this.quads = List.copyOf(quads);
		this.extents = haloType < 0 ? CuboidItemModelWrapper.computeExtents(quads)
				: new Vector3fc[] { new Vector3f(-0.7F, -0.7F, 0), new Vector3f(1.7F, 1.7F, 1) };
		this.haloType = haloType;
		this.alpha = alpha;
	}

	public record Arguments(boolean gui, AvaritiaShaders.Parameters parameters) {
	}

	@Override
	public void submit(Arguments arguments, PoseStack poses, SubmitNodeCollector collector,
			int light, int overlay, boolean foil, int outlineColor) {
		if (!quads.isEmpty()) {
			QuadInstance instance = new QuadInstance();
			instance.setColor(-1);
			instance.setLightCoords(light);
			instance.setOverlayCoords(overlay);
			collector.submitCustomGeometry(poses, AvaritiaRenderTypes.cosmic(arguments.parameters(), false), (pose, vertices) -> {
				for (BakedQuad quad : quads) {
					vertices.putBakedQuad(pose, quad, instance);
				}
			});
		}
		if (haloType >= 0 && arguments.gui()) {
			poses.pushPose();
			float size = haloType == 0 ? 2.25F : haloType == 1 ? 2.0F : 1.5F;
			poses.scale(size, size, 1);
			poses.translate(haloType == 0 ? -0.295F : haloType == 1 ? -0.25F : -0.17F,
					haloType == 0 ? -0.265F : haloType == 1 ? -0.255F : -0.155F, 0);
			int color = ((int) (alpha * 255) << 24) | (haloType == 1 ? 0xFFFFFF : 0);
			TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager()
					.get(new SpriteId(TextureAtlas.LOCATION_ITEMS, haloType == 1 ? HALO_NOISE : HALO));
			collector.order(-1).submitCustomGeometry(poses, AvaritiaRenderTypes.halo(TextureAtlas.LOCATION_ITEMS),
					(pose, vertices) -> {
						vertex(pose, vertices, sprite, 0, 0, 0, 1, color);
						vertex(pose, vertices, sprite, 1, 0, 1, 1, color);
						vertex(pose, vertices, sprite, 1, 1, 1, 0, color);
						vertex(pose, vertices, sprite, 0, 1, 0, 0, color);
					});
			poses.popPose();
		}
	}

	private static void vertex(PoseStack.Pose pose, VertexConsumer vertices, TextureAtlasSprite sprite,
			float x, float y, float u, float v, int color) {
		vertices.addVertex(pose, x, y, 0.5F).setColor(color).setUv(sprite.getU(u), sprite.getV(v))
				.setOverlay(0).setLight(0xF000F0).setNormal(pose, 0, 0, 1);
	}

	public Vector3fc[] extents() {
		return extents;
	}

	@Override
	public void getExtents(Consumer<Vector3fc> consumer) {
		for (Vector3fc extent : extents) {
			consumer.accept(extent);
		}
	}

	@Override
	public Arguments extractArgument(ItemStack stack) {
		return new Arguments(false, new AvaritiaShaders.Parameters(AvaritiaShaders.time(), 0, 0, 1, 1));
	}
}
