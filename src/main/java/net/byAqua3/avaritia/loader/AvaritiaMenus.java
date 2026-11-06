package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.inventory.MenuExtremeCrafting;
import net.byAqua3.avaritia.inventory.MenuInfinityChest;
import net.byAqua3.avaritia.inventory.MenuNeutronCollector;
import net.byAqua3.avaritia.inventory.MenuNeutroniumCompressor;
import net.byAqua3.avaritia.tile.TileExtremeCraftingTable;
import net.byAqua3.avaritia.tile.TileNeutronCollector;
import net.byAqua3.avaritia.tile.TileNeutroniumCompressor;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.SimpleContainerData;

public class AvaritiaMenus {

	/**
	 * Menus are rebuilt on the client from the block position carried by the open
	 * packet, so both sides must bind the same ContainerData instance: the one
	 * owned by the block entity. Handing the client a fresh SimpleContainerData
	 * leaves its progress values at 0 forever — the server only pushes updates for
	 * slots the menu registered, and the initial sync happens before any tick.
	 */
	private static final net.minecraft.world.inventory.ContainerData EMPTY_DATA = new SimpleContainerData(5);

	public static final ExtendedMenuType<MenuExtremeCrafting, BlockPos> EXTREME_CRAFTING = Registry.register(
			BuiltInRegistries.MENU, Avaritia.id("extreme_crafting_table"),
			new ExtendedMenuType<MenuExtremeCrafting, BlockPos>(
					(id, inventory, pos) -> new MenuExtremeCrafting(id, inventory,
							(TileExtremeCraftingTable) inventory.player.level().getBlockEntity(pos)),
					BlockPos.STREAM_CODEC));

	public static final ExtendedMenuType<MenuNeutroniumCompressor, BlockPos> COMPRESSOR = Registry.register(
			BuiltInRegistries.MENU, Avaritia.id("compressor"),
			new ExtendedMenuType<MenuNeutroniumCompressor, BlockPos>(
					(id, inventory, pos) -> new MenuNeutroniumCompressor(id, inventory,
							(TileNeutroniumCompressor) inventory.player.level().getBlockEntity(pos),
							dataOf(inventory.player.level().getBlockEntity(pos))),
					BlockPos.STREAM_CODEC));

	public static final ExtendedMenuType<MenuNeutronCollector, BlockPos> NEUTRON_COLLECTOR = Registry.register(
			BuiltInRegistries.MENU, Avaritia.id("neutron_collector"),
			new ExtendedMenuType<MenuNeutronCollector, BlockPos>(
					(id, inventory, pos) -> new MenuNeutronCollector(id, inventory,
							(TileNeutronCollector) inventory.player.level().getBlockEntity(pos),
							collectorDataOf(inventory.player.level().getBlockEntity(pos))),
					BlockPos.STREAM_CODEC));

	public static final ExtendedMenuType<MenuInfinityChest, BlockPos> INFINITY_CHEST = Registry.register(
			BuiltInRegistries.MENU, Avaritia.id("infinity_chest"),
			new ExtendedMenuType<>(MenuInfinityChest::new, BlockPos.STREAM_CODEC));

	private static net.minecraft.world.inventory.ContainerData dataOf(
			net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
		return blockEntity instanceof TileNeutroniumCompressor compressor ? compressor.dataAccess : EMPTY_DATA;
	}

	private static net.minecraft.world.inventory.ContainerData collectorDataOf(
			net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
		return blockEntity instanceof TileNeutronCollector collector ? collector.dataAccess : EMPTY_DATA;
	}

	public static void registerMenus() {
	}
}
