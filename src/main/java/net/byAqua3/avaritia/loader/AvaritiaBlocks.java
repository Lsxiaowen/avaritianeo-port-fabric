package net.byAqua3.avaritia.loader;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import java.util.function.Function;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.block.BlockCustomCraftingTable;
import net.byAqua3.avaritia.block.BlockExtremeCraftingTable;
import net.byAqua3.avaritia.block.BlockInfinityChest;
import net.byAqua3.avaritia.block.BlockNeutronCollector;
import net.byAqua3.avaritia.block.BlockNeutroniumCompressor;
import net.byAqua3.avaritia.item.ItemInfinityChest;
import net.byAqua3.avaritia.tile.TileExtremeCraftingTable;
import net.byAqua3.avaritia.tile.TileInfinityChest;
import net.byAqua3.avaritia.tile.TileNeutronCollector;
import net.byAqua3.avaritia.tile.TileNeutroniumCompressor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class AvaritiaBlocks {

	private static <T extends Block> T registerBlock(String path, Function<Block.Properties, T> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Avaritia.id(path));
		T block = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}

	private static Item registerItem(String path, Function<Item.Properties, Item> factory, Rarity rarity) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Avaritia.id(path));
		Item.Properties properties = new Item.Properties().setId(key);
		if (rarity != null) {
			properties.rarity(rarity);
		}
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties));
	}

	/**
	 * 26.1 no longer derives a {@code block.} translation prefix for BlockItem on
	 * its own, so the prefix has to be requested explicitly or the item falls back
	 * to {@code item.avaritia.<name>} and shows a raw key.
	 */
	private static Item registerBlockItem(String path, Block block, Rarity rarity) {
		return registerItem(path, p -> new BlockItem(block, p.useBlockDescriptionPrefix()), rarity);
	}

	public static final Block CRYSTAL_MATRIX_BLOCK = registerBlock("crystal_matrix_block", Block::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(50.0F, 2000.0F).sound(SoundType.METAL));
	public static final Block NEUTRONIUM_BLOCK = registerBlock("neutronium_block", Block::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(50.0F, 2000.0F).sound(SoundType.METAL));
	public static final Block INFINITY_BLOCK = registerBlock("infinity_block", Block::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(50.0F, 2000.0F).sound(SoundType.METAL));
	public static final Block COMPRESSED_CRAFTING_TABLE = registerBlock("compressed_crafting_table", BlockCustomCraftingTable::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).requiresCorrectToolForDrops().strength(4.0F).sound(SoundType.WOOD));
	public static final Block DOUBLE_COMPRESSED_CRAFTING_TABLE = registerBlock("double_compressed_crafting_table", BlockCustomCraftingTable::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).requiresCorrectToolForDrops().strength(20.0F).sound(SoundType.WOOD));
	public static final Block EXTREME_CRAFTING_TABLE = registerBlock("extreme_crafting_table", BlockExtremeCraftingTable::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).requiresCorrectToolForDrops().strength(20.0F, 2000.0F).sound(SoundType.GLASS));
	public static final Block COMPRESSOR = registerBlock("compressor", BlockNeutroniumCompressor::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).requiresCorrectToolForDrops().strength(20.0F).sound(SoundType.GLASS));
	public static final Block NEUTRON_COLLECTOR = registerBlock("neutron_collector", BlockNeutronCollector::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).requiresCorrectToolForDrops().strength(20.0F).sound(SoundType.GLASS));
	public static final Block INFINITY_CHEST = registerBlock("infinity_chest", BlockInfinityChest::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(50.0F, 2000.0F).sound(SoundType.METAL));

	public static final Item CRYSTAL_MATRIX_BLOCK_ITEM = registerBlockItem("crystal_matrix_block", CRYSTAL_MATRIX_BLOCK, Rarity.RARE);
	public static final Item NEUTRONIUM_BLOCK_ITEM = registerBlockItem("neutronium_block", NEUTRONIUM_BLOCK, Rarity.UNCOMMON);
	public static final Item INFINITY_BLOCK_ITEM = registerBlockItem("infinity_block", INFINITY_BLOCK, AvaritiaItems.COSMIC_RARITY);
	public static final Item COMPRESSED_CRAFTING_TABLE_ITEM = registerBlockItem("compressed_crafting_table", COMPRESSED_CRAFTING_TABLE, null);
	public static final Item DOUBLE_COMPRESSED_CRAFTING_TABLE_ITEM = registerBlockItem("double_compressed_crafting_table", DOUBLE_COMPRESSED_CRAFTING_TABLE, null);
	public static final Item EXTREME_CRAFTING_TABLE_ITEM = registerBlockItem("extreme_crafting_table", EXTREME_CRAFTING_TABLE, Rarity.RARE);
	public static final Item COMPRESSOR_ITEM = registerBlockItem("compressor", COMPRESSOR, Rarity.RARE);
	public static final Item NEUTRON_COLLECTOR_ITEM = registerBlockItem("neutron_collector", NEUTRON_COLLECTOR, Rarity.RARE);
	public static final Item INFINITY_CHEST_ITEM = registerItem("infinity_chest",
			p -> new ItemInfinityChest(INFINITY_CHEST, p.useBlockDescriptionPrefix()), AvaritiaItems.COSMIC_RARITY);

	public static final BlockEntityType<TileExtremeCraftingTable> EXTREME_CRAFTING_TABLE_TILE = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE, Avaritia.id("extreme_crafting_table"),
			FabricBlockEntityTypeBuilder.create(TileExtremeCraftingTable::new, EXTREME_CRAFTING_TABLE).build());
	public static final BlockEntityType<TileNeutroniumCompressor> COMPRESSOR_TILE = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE, Avaritia.id("compressor"),
			FabricBlockEntityTypeBuilder.create(TileNeutroniumCompressor::new, COMPRESSOR).build());
	public static final BlockEntityType<TileNeutronCollector> NEUTRON_COLLECTOR_TILE = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE, Avaritia.id("neutron_collector"),
			FabricBlockEntityTypeBuilder.create(TileNeutronCollector::new, NEUTRON_COLLECTOR).build());
	public static final BlockEntityType<TileInfinityChest> INFINITY_CHEST_TILE = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE, Avaritia.id("infinity_chest"),
			FabricBlockEntityTypeBuilder.create(TileInfinityChest::new, INFINITY_CHEST).build());

	public static void registerBlocks() {
	}
}



