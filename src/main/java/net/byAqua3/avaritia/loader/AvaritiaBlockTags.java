package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.Avaritia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AvaritiaBlockTags {

	public static final TagKey<Block> INCORRECT_FOR_INFINITY_TOOL = TagKey.create(
			Registries.BLOCK,
			Avaritia.id("incorrect_for_infinity_tool"));

	public static final TagKey<Block> INFINITY_AXE = TagKey.create(
			Registries.BLOCK,
			Avaritia.id("mineable/infinity_axe"));
	public static final TagKey<Block> INFINITY_PICKAXE = TagKey.create(
			Registries.BLOCK,
			Avaritia.id("mineable/infinity_pickaxe"));
	public static final TagKey<Block> INFINITY_SHOVEL = TagKey.create(
			Registries.BLOCK,
			Avaritia.id("mineable/infinity_shovel"));

	private AvaritiaBlockTags() {
	}
}
