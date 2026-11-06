package net.byAqua3.avaritia.item;

import java.util.ArrayList;
import java.util.List;

import net.byAqua3.avaritia.loader.AvaritiaBlockTags;
import net.byAqua3.avaritia.loader.AvaritiaDataComponents;
import net.byAqua3.avaritia.loader.AvaritiaToolMaterials;
import net.byAqua3.avaritia.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemInfinityShovel extends ShovelItem {

	public ItemInfinityShovel(Item.Properties properties) {
		super(AvaritiaToolMaterials.INFINITY, 16.0F, -2.0F,
				properties.component(AvaritiaDataComponents.DESTROYER, false));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (stack.getOrDefault(AvaritiaDataComponents.DESTROYER, false) && state.is(AvaritiaBlockTags.INFINITY_SHOVEL)) {
			return Float.MAX_VALUE;
		}
		return super.getDestroySpeed(stack, state) * 5.0F;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		return player != null && player.isShiftKeyDown()
				? use(context.getLevel(), player, context.getHand()) : super.useOn(context);
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity attacker) {
		if (entity != null && stack.getOrDefault(AvaritiaDataComponents.DESTROYER, false)) {
			ItemUtils.launchTarget(entity, attacker);
		}
		super.hurtEnemy(stack, entity, attacker);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity livingEntity) {
		if (!level.isClientSide() && livingEntity instanceof Player player && stack.getOrDefault(AvaritiaDataComponents.DESTROYER, false)) {
			List<ItemStack> drops = new ArrayList<>();

			for (int x = -7; x <= 8; x++) {
				for (int y = -7; y <= 8; y++) {
					for (int z = -7; z <= 8; z++) {
						BlockPos rangePos = new BlockPos(Mth.floor(pos.getX() + x), Mth.floor(pos.getY() + y), Mth.floor(pos.getZ() + z));
						BlockState rangeState = level.getBlockState(rangePos);
						if (!rangeState.isAir() && rangeState.is(AvaritiaBlockTags.INFINITY_SHOVEL)) {
							if (!level.isClientSide() && !player.isCreative()) {
								List<ItemStack> blockDrops = Block.getDrops(rangeState, (ServerLevel) level, pos, null);
								if (!blockDrops.isEmpty()) {
									drops.addAll(blockDrops);
								} else {
									drops.add(new ItemStack(rangeState.getBlock()));
								}
							}
							level.destroyBlock(rangePos, false);
						}
					}
				}
			}
			if (!level.isClientSide() && !drops.isEmpty()) {
				ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), ItemMatterCluster.makeCluster(drops));
				itemEntity.setDefaultPickUpDelay();
				level.addFreshEntity(itemEntity);
			}
		}
		return super.mineBlock(stack, level, state, pos, livingEntity);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			if (!level.isClientSide()) {
				stack.update(AvaritiaDataComponents.DESTROYER, false, destroyer -> !destroyer);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}
