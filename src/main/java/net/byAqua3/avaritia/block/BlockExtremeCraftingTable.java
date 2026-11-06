package net.byAqua3.avaritia.block;

import net.minecraft.world.InteractionResult;

import net.byAqua3.avaritia.inventory.MenuExtremeCrafting;
import net.byAqua3.avaritia.tile.TileExtremeCraftingTable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockExtremeCraftingTable extends Block implements EntityBlock {
	
	public static final Component TITLE = Component.translatable("avaritia:container.extreme_crafting_table.title");

	public BlockExtremeCraftingTable(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileExtremeCraftingTable(pos, state);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		// Menu opening only happens server-side; the client gets the screen through
		// the open-screen packet, so returning early here would break the sequence.
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TileExtremeCraftingTable tile) {
			net.byAqua3.avaritia.util.MenuHelper.open(player, pos, TITLE,
					(id, inventory, menuPos) -> new MenuExtremeCrafting(id, inventory, tile));
		}
		return InteractionResult.SUCCESS;
	}
	
}



