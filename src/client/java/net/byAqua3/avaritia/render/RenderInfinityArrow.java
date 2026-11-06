package net.byAqua3.avaritia.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.entity.EntityInfinityArrow;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * Infinity arrow renderer.
 *
 * 26.1 replaced the old immediate-mode {@code render(..., MultiBufferSource, ...)}
 * with a two-phase pipeline: {@code extractRenderState} copies what the renderer
 * needs into a render state, and {@code submit} records draw calls through
 * {@code SubmitNodeCollector}. Custom geometry therefore goes through
 * {@code submitCustomGeometry} instead of writing to a VertexConsumer directly.
 */
public class RenderInfinityArrow extends EntityRenderer<EntityInfinityArrow, RenderInfinityArrow.ArrowRenderState> {

	public static final Identifier INFINITY_ARROW = Avaritia.id("textures/entity/infinity_arrow.png");

	public RenderInfinityArrow(EntityRendererProvider.Context context) {
		super(context);
	}

	public static class ArrowRenderState extends EntityRenderState {
		public float yRot;
		public float xRot;
		public float shake;
	}

	@Override
	public ArrowRenderState createRenderState() {
		return new ArrowRenderState();
	}

	@Override
	public void extractRenderState(EntityInfinityArrow entity, ArrowRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
		state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
		state.shake = entity.shakeTime - partialTick;
	}

	@Override
	public void submit(ArrowRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot));

		if (state.shake > 0.0F) {
			float wobble = -Mth.sin(state.shake * 3.0F) * state.shake;
			poseStack.mulPose(Axis.ZP.rotationDegrees(wobble));
		}

		poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
		poseStack.scale(0.05625F, 0.05625F, 0.05625F);
		poseStack.translate(-4.0F, 0.0F, 0.0F);

		RenderType renderType = RenderTypes.entityCutout(INFINITY_ARROW);
		int light = state.lightCoords;
		collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> this.buildArrow(pose, buffer, light));

		poseStack.popPose();
	}

	private void buildArrow(PoseStack.Pose pose, VertexConsumer buffer, int light) {
		vertex(pose, buffer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
		vertex(pose, buffer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
		vertex(pose, buffer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
		vertex(pose, buffer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);
		vertex(pose, buffer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
		vertex(pose, buffer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
		vertex(pose, buffer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
		vertex(pose, buffer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);

		PoseStack fins = new PoseStack();
		fins.last().set(pose);
		for (int i = 0; i < 4; i++) {
			fins.mulPose(Axis.XP.rotationDegrees(90));
			vertex(fins.last(), buffer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
			vertex(fins.last(), buffer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
			vertex(fins.last(), buffer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
			vertex(fins.last(), buffer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
		}
	}

	private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, int x, int y, int z,
			float u, float v, int normalX, int normalY, int normalZ, int light) {
		buffer.addVertex(pose, (float) x, (float) y, (float) z)
				.setColor(-1)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(light)
				.setNormal(pose, (float) normalX, (float) normalZ, (float) normalY);
	}
}
