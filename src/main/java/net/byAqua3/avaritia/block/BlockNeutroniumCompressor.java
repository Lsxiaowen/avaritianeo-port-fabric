package net.byAqua3.avaritia.block;

import net.minecraft.world.InteractionResult;

import java.util.List;

import net.byAqua3.avaritia.inventory.MenuNeutroniumCompressor;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.tile.TileNeutroniumCompressor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockNeutroniumCompressor extends BlockMachine {

	public static final Component TITLE = Component.translatable("avaritia:container.neutronium_compressor.title");

	public BlockNeutroniumCompressor(Properties properties) {
		super(properties, () -> AvaritiaBlocks.COMPRESSOR_TILE);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		// Menu opening only happens server-side; the client receives the screen via
		// the open-screen packet, so returning early on the client breaks the flow.
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TileNeutroniumCompressor tile) {
			net.byAqua3.avaritia.util.MenuHelper.open(player, pos, TITLE,
					(id, inventory, menuPos) -> new MenuNeutroniumCompressor(id, inventory, tile, tile.dataAccess));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean movedByPiston) {
		if (true) {
			BlockEntity blockEntity = level.getBlockEntity(pos);

			if (blockEntity instanceof TileNeutroniumCompressor) {
				TileNeutroniumCompressor tile = (TileNeutroniumCompressor) blockEntity;
				List<NonNullList<ItemStack>> itemStacksList = List.of(tile.matrix.getItems(), tile.result.getItems());
				for (NonNullList<ItemStack> itemStacks : itemStacksList) {
					for (ItemStack itemStack : itemStacks) {
						if (!itemStack.isEmpty()) {
							ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(),
									itemStack);
							itemEntity.setDefaultPickUpDelay();
							level.addFreshEntity(itemEntity);
						}
					}
				}
			}
		}

	}
}

