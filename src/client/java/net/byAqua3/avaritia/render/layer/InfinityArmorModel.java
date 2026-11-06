package net.byAqua3.avaritia.render.layer;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public final class InfinityArmorModel {
	private final ModelPart shell;
	private final ModelPart skin;

	public InfinityArmorModel(boolean baby) {
		shell = layer(true, baby).bakeRoot();
		skin = layer(false, baby).bakeRoot();
	}

	public static LayerDefinition layer(boolean armor, boolean baby) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		CubeDeformation size = new CubeDeformation(armor ? 1.0F : 0.0F);
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
				.addBox(-4, -8, -4, 8, 8, 8, size), PartPose.ZERO);
		root.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0)
				.addBox(-4, -8, -4, 8, 8, 8, armor ? size.extend(-0.1F) : size.extend(0.5F)), PartPose.ZERO);
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16)
				.addBox(-4, 0, -2, 8, 12, 4, size), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16)
				.addBox(-3, -2, -2, 4, 12, 4, size), PartPose.offset(-5, 2, 0));
		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(armor ? 40 : 32, armor ? 16 : 48)
				.mirror(armor).addBox(-1, -2, -2, 4, 12, 4, size), PartPose.offset(5, 2, 0));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(armor ? 0 : 16, 48)
				.mirror(armor).addBox(-2, 0, -2, 4, 12, 4, armor ? size.extend(-0.6F) : size), PartPose.offset(1.9F, 12, 0));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, armor ? 48 : 16)
				.addBox(-2, 0, -2, 4, 12, 4, armor ? size.extend(-0.6F) : size), PartPose.offset(-1.9F, 12, 0));
		if (armor) {
			root.addOrReplaceChild("left_boot", CubeListBuilder.create().texOffs(0, 16).mirror()
					.addBox(-2, 0, -2, 4, 12, 4, size.extend(-0.1F)), PartPose.offset(1.9F, 12, 0));
			root.addOrReplaceChild("right_boot", CubeListBuilder.create().texOffs(0, 16)
					.addBox(-2, 0, -2, 4, 12, 4, size.extend(-0.1F)), PartPose.offset(-1.9F, 12, 0));
		}
		return LayerDefinition.create(baby ? HumanoidModel.BABY_TRANSFORMER.apply(mesh) : mesh, 64, 64);
	}

	public void sync(HumanoidModel<?> parent, boolean slim) {
		for (ModelPart root : List.of(shell, skin)) {
			root.loadPose(snapshot(parent.root()));
			copy(root, "head", parent.head);
			copy(root, "hat", parent.head);
			copy(root, "body", parent.body);
			copy(root, "left_arm", parent.leftArm);
			copy(root, "right_arm", parent.rightArm);
			copy(root, "left_leg", parent.leftLeg);
			copy(root, "right_leg", parent.rightLeg);
		}
		copy(shell, "left_boot", parent.leftLeg);
		copy(shell, "right_boot", parent.rightLeg);
		for (String name : List.of("left_arm", "right_arm")) {
			ModelPart arm = skin.getChild(name);
			if (slim) {
				arm.x += name.equals("left_arm") ? -0.3F : 0.3F;
				arm.xScale *= 0.8F;
				arm.yScale *= 1.01F;
			} else {
				arm.xScale *= 1.01F;
				arm.yScale *= 1.01F;
				arm.zScale *= 1.01F;
			}
		}
	}

	private static void copy(ModelPart root, String name, ModelPart source) {
		root.getChild(name).loadPose(snapshot(source));
	}

	private static PartPose snapshot(ModelPart part) {
		return new PartPose(part.x, part.y, part.z, part.xRot, part.yRot, part.zRot,
				part.xScale, part.yScale, part.zScale);
	}

	public void submitShell(PoseStack poses, OrderedSubmitNodeCollector collector, RenderType type,
			TextureAtlasSprite sprite, int light, int color, String... names) {
		submit(shell, poses, collector, type, sprite, light, color, names);
	}

	public void submitSkin(PoseStack poses, OrderedSubmitNodeCollector collector, RenderType type,
			TextureAtlasSprite sprite, int light) {
		submit(skin, poses, collector, type, sprite, light, -1,
				"head", "hat", "body", "left_arm", "right_arm", "left_leg", "right_leg");
	}

	public static void submit(ModelPart root, PoseStack poses, OrderedSubmitNodeCollector collector, RenderType type,
			TextureAtlasSprite sprite, int light, int color, String... names) {
		PartPose rootPose = snapshot(root);
		List<PartDraw> parts = new ArrayList<>(names.length);
		for (String name : names) {
			ModelPart part = root.getChild(name);
			parts.add(new PartDraw(part, snapshot(part)));
		}
		collector.submitCustomGeometry(poses, type, (pose, vertices) -> {
			PoseStack local = new PoseStack();
			local.last().set(pose);
			root.loadPose(rootPose);
			root.translateAndRotate(local);
			for (PartDraw draw : parts) {
				draw.part().loadPose(draw.pose());
				draw.part().render(local, sprite == null ? vertices : sprite.wrap(vertices), light,
						OverlayTexture.NO_OVERLAY, color);
			}
		});
	}

	private record PartDraw(ModelPart part, PartPose pose) {
	}
}
