package net.byAqua3.avaritia.loader;

import net.minecraft.world.item.ToolMaterial;

public class AvaritiaToolMaterials {

	/**
	 * Parameters: incorrect-for-drops tag, durability, mining speed, attack damage
	 * bonus, enchantment value, repair ingredient tag.
	 *
	 * The enchantment value must be strictly positive — {@code Enchantable}'s
	 * constructor rejects 0, and {@code applyCommonProperties} runs for every
	 * material-based item, so a 0 here fails during static init of the item
	 * registry.
	 */
	public static final ToolMaterial INFINITY = new ToolMaterial(
			AvaritiaBlockTags.INCORRECT_FOR_INFINITY_TOOL,
			9999,
			9999.0F,
			-1.0F,
			1000,
			AvaritiaItemTags.IMMORTAL);

	private AvaritiaToolMaterials() {
	}
}
