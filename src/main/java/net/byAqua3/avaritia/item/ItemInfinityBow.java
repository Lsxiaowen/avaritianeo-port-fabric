package net.byAqua3.avaritia.item;

import net.byAqua3.avaritia.entity.EntityInfinityArrow;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class ItemInfinityBow extends BowItem {

	public static final int DRAW_TIME = 8;

	public ItemInfinityBow(Item.Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 13;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.BOW;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int time) {
		if (!(livingEntity instanceof Player player) || time != 1) {
			return;
		}

		int max = this.getUseDuration(stack, player);
		float f = (float) max / (float) max;
		f = (f * f + f * 2.0F) / 3.0F;

		if (f < 0.1F) {
			return;
		}
		if (f > 1.0F) {
			f = 1.0F;
		}

		EntityInfinityArrow arrow = new EntityInfinityArrow(level, false);
		arrow.setPos(player.getX(), player.getEyeY(), player.getZ());
		arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);
		arrow.setBaseDamage(1000.0F);
		arrow.setOwner(player);

		if (f == 1.0F) {
			arrow.setCritArrow(true);
		}

		int power = EnchantmentHelper.getItemEnchantmentLevel(
				level.registryAccess().getOrThrow(Enchantments.POWER), stack);
		if (power > 0) {
			arrow.setBaseDamage(1000.0D + (double) power * 0.5D + 0.5D);
		}

		int punch = EnchantmentHelper.getItemEnchantmentLevel(
				level.registryAccess().getOrThrow(Enchantments.PUNCH), stack);
		if (punch > 0) {
			arrow.setKnockback(punch);
		}

		if (EnchantmentHelper.getItemEnchantmentLevel(
				level.registryAccess().getOrThrow(Enchantments.FLAME), stack) > 0) {
			arrow.igniteForSeconds(100);
		}

		arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;

		if (!level.isClientSide()) {
			level.addFreshEntity(arrow);
		}

		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
				1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);

		player.awardStat(Stats.ITEM_USED.get(this));
	}




}

