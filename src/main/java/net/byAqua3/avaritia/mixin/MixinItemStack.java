package net.byAqua3.avaritia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.byAqua3.avaritia.loader.AvaritiaItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

/**
 * Items tagged {@code avaritia:immortal} survive explosions.
 *
 * No {@code @Shadow} is used: in 26.1 {@code ItemStack.item} is a
 * {@code Holder<Item>} rather than an {@code Item}, so shadowing the old type
 * fails mixin validation. The owning stack is reachable from the injected
 * method anyway.
 */
@Mixin(ItemStack.class)
public class MixinItemStack {

	@Inject(method = { "getStyledHoverName", "getDisplayName" }, at = @At("RETURN"), cancellable = true)
	private void avaritia$cosmicName(CallbackInfoReturnable<Component> callback) {
		ItemStack self = (ItemStack) (Object) this;
		if (self.is(AvaritiaItemTags.COSMIC_RARITY)) {
			callback.setReturnValue(callback.getReturnValue().copy().withStyle(ChatFormatting.RED));
		}
	}

	@Inject(method = "canBeHurtBy", at = @At("HEAD"), cancellable = true)
	public void avaritia$canBeHurtBy(DamageSource damageSource, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		ItemStack self = (ItemStack) (Object) this;

		if (!self.isEmpty() && self.is(AvaritiaItemTags.IMMORTAL) && damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
			callbackInfoReturnable.setReturnValue(false);
		}
	}
}
