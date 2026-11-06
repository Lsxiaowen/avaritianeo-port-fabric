package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.gui.GuiExtremeCraftingTable;
import net.byAqua3.avaritia.gui.GuiInfinityChest;
import net.byAqua3.avaritia.gui.GuiNeutronCollector;
import net.byAqua3.avaritia.gui.GuiNeutroniumCompressor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Binds container menus to their screens.
 *
 * 26.1 keeps the MenuScreens registry map private and Fabric ships no public
 * replacement, so the map is opened through the access widener and filled here.
 * The raw {@code ScreenConstructor} type erases {@code create}'s first parameter
 * to {@code AbstractContainerMenu}, hence the casts inside each lambda.
 */
public class AvaritiaScreens {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void registerScreens() {
		put(AvaritiaMenus.EXTREME_CRAFTING, (menu, inventory, title) ->
				new GuiExtremeCraftingTable((net.byAqua3.avaritia.inventory.MenuExtremeCrafting) menu, inventory, title));
		put(AvaritiaMenus.COMPRESSOR, (menu, inventory, title) ->
				new GuiNeutroniumCompressor((net.byAqua3.avaritia.inventory.MenuNeutroniumCompressor) menu, inventory, title));
		put(AvaritiaMenus.NEUTRON_COLLECTOR, (menu, inventory, title) ->
				new GuiNeutronCollector((net.byAqua3.avaritia.inventory.MenuNeutronCollector) menu, inventory, title));
		put(AvaritiaMenus.INFINITY_CHEST, (menu, inventory, title) ->
				new GuiInfinityChest((net.byAqua3.avaritia.inventory.MenuInfinityChest) menu, inventory, title));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void put(MenuType<?> type, MenuScreens.ScreenConstructor constructor) {
		MenuScreens.SCREENS.put(type, constructor);
	}

	private AvaritiaScreens() {
	}
}
