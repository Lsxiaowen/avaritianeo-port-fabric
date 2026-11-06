package net.byAqua3.avaritia.mixin;

import net.byAqua3.avaritia.render.layer.LayerInfinityArmor;
import net.byAqua3.avaritia.render.layer.InfinityArmorState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject(method = "<init>", at = @At("TAIL"))
	private void avaritia$addArmorLayer(EntityRendererProvider.Context context, EntityModel<?> model,
			float shadowRadius, CallbackInfo callbackInfo) {
		if (model instanceof HumanoidModel<?>) {
			LivingEntityRenderer renderer = (LivingEntityRenderer) (Object) this;
			renderer.addLayer(new LayerInfinityArmor(renderer));
		}
	}

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
	private void avaritia$extractArmorState(LivingEntity entity, LivingEntityRenderState state,
			float partialTick, CallbackInfo callbackInfo) {
		if (state instanceof InfinityArmorState visual) {
			visual.avaritia$setFlying(entity instanceof Player player && player.getAbilities().flying);
			visual.avaritia$setView(entity.getYRot() * Mth.DEG_TO_RAD, -entity.getXRot() * Mth.DEG_TO_RAD);
		}
	}
}
