package net.byAqua3.avaritia;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.byAqua3.avaritia.loader.AvaritiaArmorMaterials;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.byAqua3.avaritia.loader.AvaritiaConfigs;
import net.byAqua3.avaritia.loader.AvaritiaDataComponents;
import net.byAqua3.avaritia.loader.AvaritiaEntities;
import net.byAqua3.avaritia.loader.AvaritiaEvents;
import net.byAqua3.avaritia.loader.AvaritiaItems;
import net.byAqua3.avaritia.loader.AvaritiaMenus;
import net.byAqua3.avaritia.loader.AvaritiaNetworks;
import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.byAqua3.avaritia.loader.AvaritiaSounds;
import net.byAqua3.avaritia.loader.AvaritiaTabs;
import net.byAqua3.avaritia.loader.AvaritiaTriggers;
import net.minecraft.resources.Identifier;

import net.fabricmc.api.ModInitializer;

public class Avaritia implements ModInitializer {

	public static final String MODID = "avaritia";
	public static final String NAME = "Avaritia";
	public static final String VERSION = "1.0.0-rc";
	public static final String[] AUTHORS = new String[] { "xiaowen" };

	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		AvaritiaConfigs.registerConfigs();
		AvaritiaDataComponents.registerDataComponents();
		AvaritiaArmorMaterials.registerArmorMaterials();
		AvaritiaItems.registerItems();
		AvaritiaBlocks.registerBlocks();
		AvaritiaEntities.registerEntities();
		AvaritiaRecipes.registerRecipes();
		AvaritiaSounds.registerSounds();
		AvaritiaTabs.registerTabs();
		AvaritiaMenus.registerMenus();
		AvaritiaTriggers.registerTriggers();
		AvaritiaNetworks.registerNetworks();
		AvaritiaEvents.registerEvents();
		LOGGER.info("Avaritia initialized");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
}

