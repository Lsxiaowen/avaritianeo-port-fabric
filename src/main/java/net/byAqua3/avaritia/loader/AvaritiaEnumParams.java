package net.byAqua3.avaritia.loader;

import net.minecraft.world.item.Rarity;

public class AvaritiaEnumParams {

	public static final CosmicRarityProxy COSMIC_RARITY_ENUM_PROXY = new CosmicRarityProxy();

	public static class CosmicRarityProxy {
		public Rarity getValue() {
			return Rarity.EPIC;
		}
	}
}
