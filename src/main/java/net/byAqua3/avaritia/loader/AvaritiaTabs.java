package net.byAqua3.avaritia.loader;

import java.util.ArrayList;
import java.util.List;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.item.ItemInfinitySingularity;
import net.byAqua3.avaritia.item.ItemJsonSingularity;
import net.byAqua3.avaritia.singularity.Singularity;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AvaritiaTabs {

	public static final List<Item> BLACK_ITEMS = new ArrayList<>();

	public static final ResourceKey<CreativeModeTab> AVARITIA_TAB_KEY =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, Avaritia.id(Avaritia.MODID));

	public static final CreativeModeTab AVARITIA_TAB = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			AVARITIA_TAB_KEY,
			FabricCreativeModeTab.builder()
					.title(Component.translatable("itemGroup.avaritia"))
					.icon(() -> new ItemStack(AvaritiaItems.INFINITY_CATALYST))
					.build());

	public static void registerTabs() {
		CreativeModeTabEvents.modifyOutputEvent(AVARITIA_TAB_KEY).register(output -> {
			for (Item item : BuiltInRegistries.ITEM) {
				// Only this mod's items belong in this tab; the registry also holds
				// minecraft:air and every other mod's items, and accepting an empty
				// stack throws.
				if (!isOurItem(item)) {
					continue;
				}
				if (BLACK_ITEMS.contains(item) || item instanceof ItemJsonSingularity) {
					continue;
				}

				if (item instanceof ItemInfinitySingularity) {
					for (Singularity singularity : AvaritiaSingularities.getInstance().getSingularities()) {
						ItemStack itemStack = new ItemStack(AvaritiaItems.JSON_SINGULARITY);
						itemStack.set(AvaritiaDataComponents.SINGULARITY_ID, singularity.getId());
						output.accept(itemStack);
					}
				}

				output.accept(new ItemStack(item));
			}
		});
	}

	private static boolean isOurItem(Item item) {
		Identifier id = BuiltInRegistries.ITEM.getKey(item);
		return id != null && Avaritia.MODID.equals(id.getNamespace());
	}
}
