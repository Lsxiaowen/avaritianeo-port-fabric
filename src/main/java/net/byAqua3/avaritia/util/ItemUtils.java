package net.byAqua3.avaritia.util;

import net.byAqua3.avaritia.loader.AvaritiaItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ItemUtils {

	public static void launchTarget(LivingEntity target, LivingEntity attacker) {
		if (!target.level().isClientSide()) {
			float yaw = attacker.getYRot() * (float) Math.PI / 180.0F;
			target.setDeltaMovement(target.getDeltaMovement().add(-Math.sin(yaw) * 5.0D, 2.0D, Math.cos(yaw) * 5.0D));
			target.hurtMarked = true;
		}
	}

	/**
	 * True when the player wears the full infinity set. Uses {@code ItemStack#is}
	 * rather than reference comparison so it also matches stacks produced from
	 * recipes or NBT where the item instance is the same registry object but the
	 * stack is not.
	 */
	public static boolean isInfinityArmor(Player player) {
		return player.getItemBySlot(EquipmentSlot.HEAD).is(AvaritiaItems.INFINITY_HELMET)
				&& player.getItemBySlot(EquipmentSlot.CHEST).is(AvaritiaItems.INFINITY_CHESTPLATE)
				&& player.getItemBySlot(EquipmentSlot.LEGS).is(AvaritiaItems.INFINITY_LEGGINGS)
				&& player.getItemBySlot(EquipmentSlot.FEET).is(AvaritiaItems.INFINITY_BOOTS);
	}
}
