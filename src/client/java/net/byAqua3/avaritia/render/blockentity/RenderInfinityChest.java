package net.byAqua3.avaritia.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.tile.TileInfinityChest;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Infinity chest renderer.
 *
 * The chest texture is a 64x576 animated atlas (9 frames). Drawing it through a
 * plain RenderType would stretch the whole atlas across the cube, which is what
 * produced the garbled look. Submitting the model together with a SpriteId binds
 * the correct atlas region and lets the animation frames play.
 */
public class RenderInfinityChest implements BlockEntityRenderer<TileInfinityChest, RenderInfinityChest.InfinityChestRenderState> {

	private static final SpriteId CHEST_SPRITE =
			new SpriteId(Sheets.CHEST_SHEET, Avaritia.id("entity/chest/infinity_chest"));

	private final ChestModel model;
	private final SpriteGetter sprites;

	public RenderInfinityChest(BlockEntityRendererProvider.Context context) {
		this.model = new ChestModel(context.bakeLayer(ModelLayers.CHEST));
		this.sprites = context.sprites();
	}

	public static LayerDefinition createSingleBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("bottom",
				CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
		root.addOrReplaceChild("lid",
				CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F),
				PartPose.offset(0.0F, 9.0F, 1.0F));
		root.addOrReplaceChild("lock",
				CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F),
				PartPose.offset(0.0F, 9.0F, 1.0F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	public static class InfinityChestRenderState extends BlockEntityRenderState {
		public float open;
		public Direction facing = Direction.SOUTH;
	}

	@Override
	public InfinityChestRenderState createRenderState() {
		return new InfinityChestRenderState();
	}

	@Override
	public void extractRenderState(TileInfinityChest blockEntity, InfinityChestRenderState state, float partialTick,
			Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
		BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
		state.open = blockEntity.getOpenNess(partialTick);
		BlockState blockState = blockEntity.getBlockState();
		if (blockState.hasProperty(ChestBlock.FACING)) {
			state.facing = blockState.getValue(ChestBlock.FACING);
		}
	}

	@Override
	public void submit(InfinityChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
			CameraRenderState camera) {
		poseStack.pushPose();

		float yRot = state.facing.toYRot();
		poseStack.translate(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
		poseStack.translate(-0.5F, -0.5F, -0.5F);

		float openness = 1.0F - state.open;
		openness = 1.0F - openness * openness * openness;

		collector.submitModel(this.model, openness, poseStack, state.lightCoords,
				OverlayTexture.NO_OVERLAY, -1, CHEST_SPRITE, this.sprites, 0, state.breakProgress);

		poseStack.popPose();
	}
}
