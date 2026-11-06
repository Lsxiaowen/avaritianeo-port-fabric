package net.byAqua3.avaritia.item;

import net.byAqua3.avaritia.loader.AvaritiaToolMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ItemInfinityHoe extends HoeItem {

	public ItemInfinityHoe(Item.Properties properties) {
		super(AvaritiaToolMaterials.INFINITY, 29.0F, 0.0F, properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	private boolean applyBonemeal(Level level, BlockPos pos, Player player) {
		BlockState blockState = level.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof BonemealableBlock bonemealable && !(block instanceof GrassBlock) && !(block instanceof TallGrassBlock) && !(block instanceof DoublePlantBlock)) {
			if (bonemealable.isValidBonemealTarget(level, pos, blockState)) {
				if (level instanceof ServerLevel serverLevel && bonemealable.isBonemealSuccess(level, level.getRandom(), pos, blockState)) {
					bonemealable.performBonemeal(serverLevel, level.getRandom(), pos, blockState);
				}
			} else {
				level.destroyBlock(pos, true);
				for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
					if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == block) {
						level.setBlockAndUpdate(pos, block.defaultBlockState());
						if (!player.isCreative()) {
							stack.shrink(1);
						}
						break;
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}

		if (player.isShiftKeyDown()) {
			if (this.applyBonemeal(level, blockPos, player)) {
				return InteractionResult.SUCCESS_SERVER;
			}
			return super.useOn(context);
		}

		boolean acted = false;
		for (int x = -4; x <= 4; x++) {
			for (int z = -4; z <= 4; z++) {
				BlockPos rangePos = new BlockPos(Mth.floor(blockPos.getX() + x), Mth.floor(blockPos.getY()), Mth.floor(blockPos.getZ() + z));
				BlockState rangeState = level.getBlockState(rangePos);

				if (this.applyBonemeal(level, rangePos, player)) {
					acted = true;
					continue;
				}
				if (this.applyBonemeal(level, rangePos.above(), player)) {
					acted = true;
					continue;
				}

				UseOnContext rangeContext = new UseOnContext(player, context.getHand(),
						new net.minecraft.world.phys.BlockHitResult(
								net.minecraft.world.phys.Vec3.atCenterOf(rangePos),
								net.minecraft.core.Direction.UP, rangePos, false));
				InteractionResult tilled = super.useOn(rangeContext);
				if (tilled.consumesAction()) {
					acted = true;
				}
			}
		}
		return acted ? InteractionResult.SUCCESS_SERVER : InteractionResult.PASS;
	}
}


