package net.byAqua3.avaritia.render.layer;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Infinity armor wings.
 *
 * Two zero-thickness 32x32 planes, baked straight from a LayerDefinition rather
 * than going through the model-layer registry: {@code bakeRoot()} is pure CPU,
 * so no registration event (which Fabric does not expose) is required.
 *
 * The 0.4 * PI yaw matches the original spread angle.
 */
public final class ModelInfinityWings {

	public static final float SPREAD = (float) (Math.PI * 0.4);

	private ModelInfinityWings() {
	}

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		CubeDeformation none = new CubeDeformation(0.0F);

		root.addOrReplaceChild("left_wing",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(0.0F, -11.6F, 0.0F, 0.0F, 32.0F, 32.0F, none),
				PartPose.offsetAndRotation(-1.5F, 0.0F, 2.0F, 0.0F, SPREAD, 0.0F));

		root.addOrReplaceChild("right_wing",
				CubeListBuilder.create().texOffs(0, 0).mirror()
						.addBox(0.0F, -11.6F, 0.0F, 0.0F, 32.0F, 32.0F, none),
				PartPose.offsetAndRotation(1.5F, 0.0F, 2.0F, 0.0F, -SPREAD, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

}
