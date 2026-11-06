package net.byAqua3.avaritia.mixin;

import net.byAqua3.avaritia.loader.AvaritiaItemTags;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class MixinGui {
	@Redirect(method = "extractSelectedItemName", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;getHoverName()Lnet/minecraft/network/chat/Component;"))
	private Component avaritia$selectedCosmicName(ItemStack stack) {
		return stack.is(AvaritiaItemTags.COSMIC_RARITY) ? stack.getStyledHoverName() : stack.getHoverName();
	}
}
