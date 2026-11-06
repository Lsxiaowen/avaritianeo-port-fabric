package net.byAqua3.avaritia.item;

import net.byAqua3.avaritia.damage.DamageSourceInfinity;
import net.byAqua3.avaritia.util.ItemUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemInfinitySword extends Item {

	public ItemInfinitySword(Item.Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity attacker) {
		DamageSourceInfinity damageSource = new DamageSourceInfinity(attacker);
		if (!entity.level().isClientSide()) {
			if (entity instanceof Player player && ItemUtils.isInfinityArmor(player)) {
				entity.hurt(damageSource, 4.0F);
				return;
			}
			entity.hurt(damageSource, Float.MAX_VALUE);
			entity.setHealth(0.0F);
			entity.die(damageSource);
		}
	}
}
