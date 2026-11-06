package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.Avaritia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AvaritiaItemTags {

	public static final TagKey<Item> IMMORTAL = TagKey.create(Registries.ITEM, Avaritia.id("immortal"));
	public static final TagKey<Item> FAST_PICKUP = TagKey.create(Registries.ITEM, Avaritia.id("fast_pickup"));
	public static final TagKey<Item> COSMIC_RARITY = TagKey.create(Registries.ITEM, Avaritia.id("cosmic_rarity"));

	private AvaritiaItemTags() {
	}
}
