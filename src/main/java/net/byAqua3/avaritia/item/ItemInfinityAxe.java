package net.byAqua3.avaritia.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.byAqua3.avaritia.loader.AvaritiaBlockTags;
import net.byAqua3.avaritia.loader.AvaritiaToolMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemInfinityAxe extends AxeItem {

	public static Set<BlockSwapper> BlockSwappers = ConcurrentHashMap.newKeySet();

	public ItemInfinityAxe(Item.Properties properties) {
		super(AvaritiaToolMaterials.INFINITY, 29.0F, -3.0F, properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity livingEntity) {
		if (livingEntity instanceof Player player && !player.isShiftKeyDown()) {
			if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
				BlockSwappers.add(new BlockSwapper(level, !player.isCreative(), pos, 0, new ArrayList<>()));
			}
		}
		return super.mineBlock(stack, level, state, pos, livingEntity);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockPos blockPos = player.blockPosition();
		if (player.isShiftKeyDown()) {
			List<ItemStack> drops = new ArrayList<>();

			for (int x = -7; x <= 8; x++) {
				for (int y = -7; y <= 8; y++) {
					for (int z = -7; z <= 8; z++) {
						BlockPos rangePos = new BlockPos(Mth.floor(blockPos.getX() + x), Mth.floor(blockPos.getY() + y), Mth.floor(blockPos.getZ() + z));
						BlockState rangeState = level.getBlockState(rangePos);
						if (rangeState.isAir()) {
							continue;
						}
						if (rangeState.is(Blocks.GRASS_BLOCK)) {
							level.setBlockAndUpdate(rangePos, Blocks.DIRT.defaultBlockState());
						} else if (rangeState.is(AvaritiaBlockTags.INFINITY_AXE)) {
							if (!level.isClientSide() && !player.isCreative()) {
								List<ItemStack> blockDrops = Block.getDrops(rangeState, (ServerLevel) level, blockPos, null);
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
				ItemEntity itemEntity = new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ItemMatterCluster.makeCluster(drops));
				itemEntity.setDefaultPickUpDelay();
				level.addFreshEntity(itemEntity);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static class BlockSwapper {

		private final Level level;
		private final boolean canDrop;
		private final BlockPos breakPos;
		private final int breakCount;
		private final List<BlockPos> posChecked;

		public BlockSwapper(Level level, boolean canDrop, BlockPos breakPos, int breakCount, List<BlockPos> posChecked) {
			this.level = level;
			this.canDrop = canDrop;
			this.breakPos = breakPos;
			this.breakCount = breakCount;
			this.posChecked = posChecked;
		}

		public void tick() {
			if (this.breakCount > 30) {
				return;
			}
			BlockState blockState = this.level.getBlockState(this.breakPos);
			boolean isWood = blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES);
			if (this.breakCount == 0 || (!blockState.isAir() && isWood)) {
				if (!blockState.isAir() && !this.level.isClientSide()) {
					List<ItemStack> blockDrops = Block.getDrops(blockState, (ServerLevel) this.level, this.breakPos, null);

					this.level.setBlockAndUpdate(this.breakPos, Blocks.AIR.defaultBlockState());
					this.level.gameEvent(GameEvent.BLOCK_DESTROY, this.breakPos, GameEvent.Context.of(null, blockState));

					if (this.canDrop && !blockDrops.isEmpty()) {
						for (ItemStack itemStack : blockDrops) {
							ItemEntity itemEntity = new ItemEntity(this.level, this.breakPos.getX(), this.breakPos.getY(), this.breakPos.getZ(), itemStack);
							itemEntity.setDefaultPickUpDelay();
							this.level.addFreshEntity(itemEntity);
						}
					}
				}
				for (Direction direction : Direction.values()) {
					Vec3i normal = direction.getUnitVec3i();
					BlockPos directionPos = this.breakPos.offset(normal);
					if (this.posChecked.contains(directionPos)) {
						continue;
					}
					BlockState directionBlockState = this.level.getBlockState(directionPos);
					if (!directionBlockState.isAir() && (directionBlockState.is(BlockTags.LOGS) || directionBlockState.is(BlockTags.LEAVES))) {
						this.posChecked.add(directionPos);
						BlockSwappers.add(new BlockSwapper(this.level, this.canDrop, directionPos, this.breakCount + 1, this.posChecked));
					}
				}
			}
		}
	}
}
