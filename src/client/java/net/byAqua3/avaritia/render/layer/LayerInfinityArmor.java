package net.byAqua3.avaritia.render.layer;

import java.awt.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.event.AvaritiaClientEvent;
import net.byAqua3.avaritia.loader.AvaritiaDataComponents;
import net.byAqua3.avaritia.loader.AvaritiaItems;
import net.byAqua3.avaritia.loader.AvaritiaRenderTypes;
import net.byAqua3.avaritia.loader.AvaritiaShaders;
import net.byAqua3.avaritia.mixin.PlayerModelAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

public final class LayerInfinityArmor<S extends HumanoidRenderState, M extends HumanoidModel<? super S>> extends RenderLayer<S, M> {
	private static final Identifier EYES = Avaritia.id("textures/models/armor/infinity_armor_eyes.png");
	private static final Identifier WING = Avaritia.id("textures/models/armor/infinity_armor_wing.png");
	private static final Identifier WING_GLOW = Avaritia.id("textures/models/armor/infinity_armor_wingglow.png");
	private final InfinityArmorModel adult = new InfinityArmorModel(false);
	private final InfinityArmorModel baby = new InfinityArmorModel(true);
	private final ModelPart wings = ModelInfinityWings.createLayer().bakeRoot();

	public LayerInfinityArmor(RenderLayerParent<S, M> renderer) {
		super(renderer);
	}

	private static TextureAtlasSprite sprite(String name) {
		return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS)
				.getSprite(Avaritia.id("models/armor/" + name));
	}

	@Override
	public void submit(PoseStack poses, SubmitNodeCollector collector, int light, S state, float yaw, float pitch) {
		boolean helmet = state.headEquipment.is(AvaritiaItems.INFINITY_HELMET);
		boolean chest = state.chestEquipment.is(AvaritiaItems.INFINITY_CHESTPLATE);
		boolean legs = state.legsEquipment.is(AvaritiaItems.INFINITY_LEGGINGS);
		boolean boots = state.feetEquipment.is(AvaritiaItems.INFINITY_BOOTS);
		if (!helmet && !chest && !legs && !boots) {
			return;
		}
		boolean wingsVisible = chest && state instanceof AvatarRenderState
				&& (((InfinityArmorState) state).avaritia$isFlying()
						|| state.chestEquipment.getOrDefault(AvaritiaDataComponents.FLY, false));
		submitArmor(poses, collector, light, state, helmet, chest, legs, boots, wingsVisible);
	}

	private void submitArmor(PoseStack poses, SubmitNodeCollector collector, int light, S state,
			boolean helmet, boolean chest, boolean legs, boolean boots, boolean wingsVisible) {
		M parent = getParentModel();
		InfinityArmorModel model = state.isBaby ? baby : adult;
		model.sync(parent, parent instanceof PlayerModelAccessor player && player.avaritia$isSlim());
		InfinityArmorState visual = (InfinityArmorState) state;
		float time = AvaritiaShaders.time();
		RenderType cosmic = AvaritiaRenderTypes.cosmic(new AvaritiaShaders.Parameters(time,
				visual.avaritia$getYaw(), visual.avaritia$getPitch(), 1.0F, 0.9F), true);
		TextureAtlasSprite mask = sprite("infinity_armor_mask");
		float pulse = (float) (Math.sin(time / 10.0D) * 0.5D + 0.5D);
		int glow = new Color(0.84F, 1.0F, 0.95F, (float) (Math.pow(pulse, 6) * 0.5D)).getRGB();
		var cosmicCollector = collector.order(2);
		var eyeCollector = collector.order(3);
		if (helmet) {
			model.submitShell(poses, cosmicCollector, cosmic, mask, light, -1, "head", "hat");
		}
		if (chest) {
			model.submitShell(poses, cosmicCollector, cosmic, mask, light, -1, "body", "left_arm", "right_arm");
			model.submitShell(poses, eyeCollector, AvaritiaRenderTypes.wingGlow(EYES), null, light, glow,
					"body", "left_arm", "right_arm");
		}
		if (legs) {
			model.submitShell(poses, cosmicCollector, cosmic, mask, light, -1, "left_leg", "right_leg");
			model.submitShell(poses, eyeCollector, AvaritiaRenderTypes.wingGlow(EYES), null, light, glow,
					"left_leg", "right_leg");
		}
		if (boots) {
			model.submitShell(poses, cosmicCollector, cosmic, mask, light, -1, "left_boot", "right_boot");
		}
		if (helmet && chest && legs && boots) {
			int color = Color.HSBtoRGB((System.currentTimeMillis() - AvaritiaClientEvent.lastTime) / 2000.0F, 1, 1);
			model.submitShell(poses, collector.order(4), AvaritiaRenderTypes.glow(EYES), null, light, color, "hat");
			model.submitSkin(poses, collector.order(5), cosmic, sprite("infinity_armor_mask_inv"), light);
		}
		if (wingsVisible) {
			poses.pushPose();
			parent.root().translateAndRotate(poses);
			parent.body.translateAndRotate(poses);
			if (state.isBaby) {
				poses.scale(0.5F, 0.5F, 0.5F);
			}
			InfinityArmorModel.submit(wings, poses, collector.order(0), RenderTypes.armorCutoutNoCull(WING), null,
					light, -1, "left_wing", "right_wing");
			InfinityArmorModel.submit(wings, poses, collector.order(1), AvaritiaRenderTypes.wingGlow(WING_GLOW), null,
					light, glow, "left_wing", "right_wing");
			InfinityArmorModel.submit(wings, poses, cosmicCollector, cosmic, sprite("infinity_armor_mask_wings"),
					light, -1, "left_wing", "right_wing");
			poses.popPose();
		}
	}
}
