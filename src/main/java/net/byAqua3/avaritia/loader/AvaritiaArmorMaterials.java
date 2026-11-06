package net.byAqua3.avaritia.loader;

import java.util.Map;

import net.byAqua3.avaritia.Avaritia;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class AvaritiaArmorMaterials {

	public static final ResourceKey<EquipmentAsset> INFINITY_ASSET = ResourceKey.create(
			EquipmentAssets.ROOT_ID,
			Avaritia.id("infinity"));

	/**
	 * Parameters: durability multiplier, defense per slot, enchantment value,
	 * equip sound, toughness, knockback resistance, repair tag, equipment asset.
	 *
	 * The enchantment value must be strictly positive — {@code Enchantable}
	 * rejects 0, and armor goes through the same {@code applyCommonProperties}
	 * path as tools.
	 */
	public static final ArmorMaterial INFINITY = new ArmorMaterial(
			0,
			Map.of(
					ArmorType.HELMET, 6,
					ArmorType.CHESTPLATE, 16,
					ArmorType.LEGGINGS, 12,
					ArmorType.BOOTS, 6),
			1000,
			SoundEvents.ARMOR_EQUIP_DIAMOND,
			1.0F,
			10.0F,
			ItemTags.REPAIRS_NETHERITE_ARMOR,
			INFINITY_ASSET);

	public static void registerArmorMaterials() {
	}
}
