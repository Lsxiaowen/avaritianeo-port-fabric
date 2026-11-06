package net.byAqua3.avaritia.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.entity.EntityGapingVoid;
import net.byAqua3.avaritia.loader.AvaritiaModels;
import net.byAqua3.avaritia.loader.AvaritiaRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class RenderGapingVoid extends EntityRenderer<EntityGapingVoid, RenderGapingVoid.VoidRenderState> {
	private static final Identifier VOID = Avaritia.id("textures/entity/void/void.png");
	private static final Identifier HALO = Avaritia.id("textures/entity/void/void_halo.png");

	public RenderGapingVoid(EntityRendererProvider.Context context) {
		super(context);
	}

	public static final class VoidRenderState extends EntityRenderState {
		public float scale;
		public int color;
	}

	@Override
	public VoidRenderState createRenderState() {
		return new VoidRenderState();
	}

	@Override
	public void extractRenderState(EntityGapingVoid entity, VoidRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		float age = entity.getAge() + partialTick;
		state.scale = (float) EntityGapingVoid.getVoidScale(age);
		float life = age / EntityGapingVoid.MAX_LIFETIME;
		int shade = (int) (Mth.clamp(Math.max((life - 0.95F) / 0.05F, 1.0F - life * 30.0F), 0, 1) * 255);
		state.color = 0xFF000000 | shade << 16 | shade << 8 | shade;
	}

	@Override
	public void submit(VoidRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
		float scale = state.scale;
		if (scale <= 0) {
			return;
		}
		int color = state.color;
		int light = state.lightCoords;
		double dx = state.x - camera.pos.x;
		double dy = state.y - camera.pos.y;
		double dz = state.z - camera.pos.z;
		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		double close = Mth.clamp((2.2D * scale - distance) / (2.2D * scale), 0, 1);
		float halo = (float) (0.58D * scale * (1 + Math.pow(close, 4) * 1.5D));
		poses.pushPose();
		poses.translate(0.5F, 0.5F, 0.5F);
		poses.mulPose(Axis.YP.rotation((float) Math.atan2(dx, dz)));
		poses.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(Math.atan2(Math.hypot(dx, dz), dy)) + 275));
		collector.submitCustomGeometry(poses, AvaritiaRenderTypes.voidHalo(HALO), (pose, buffer) -> {
			buffer.addVertex(pose, -halo, -halo, 0).setColor(color).setUv(0, 0).setOverlay(0).setLight(light).setNormal(pose, 0, 0, 1);
			buffer.addVertex(pose, -halo, halo, 0).setColor(color).setUv(0, 1).setOverlay(0).setLight(light).setNormal(pose, 0, 0, 1);
			buffer.addVertex(pose, halo, halo, 0).setColor(color).setUv(1, 1).setOverlay(0).setLight(light).setNormal(pose, 0, 0, 1);
			buffer.addVertex(pose, halo, -halo, 0).setColor(color).setUv(1, 0).setOverlay(0).setLight(light).setNormal(pose, 0, 0, 1);
		});
		poses.popPose();
		poses.pushPose();
		poses.translate(0.5F, 0.25F, 0.5F);
		poses.scale(scale - 0.1F, scale - 0.1F, scale - 0.1F);
		ObjMesh mesh = AvaritiaModels.voidMesh();
		collector.submitCustomGeometry(poses, AvaritiaRenderTypes.voidBody(VOID), (pose, buffer) -> mesh.render(pose, buffer, color, light));
		poses.popPose();
	}

	@Override
	public boolean shouldRender(EntityGapingVoid entity, net.minecraft.client.renderer.culling.Frustum frustum,
			double cameraX, double cameraY, double cameraZ) {
		return entity.shouldRenderAtSqrDistance(entity.distanceToSqr(cameraX, cameraY, cameraZ));
	}
}
