package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.item.ItemCrystalMatrixIngot;
import net.byAqua3.avaritia.item.ItemEndestPearl;
import net.byAqua3.avaritia.item.ItemInfinityArmor;
import net.byAqua3.avaritia.item.ItemInfinityAxe;
import net.byAqua3.avaritia.item.ItemInfinityBow;
import net.byAqua3.avaritia.item.ItemInfinityCatalyst;
import net.byAqua3.avaritia.item.ItemInfinityHoe;
import net.byAqua3.avaritia.item.ItemInfinityIngot;
import net.byAqua3.avaritia.item.ItemInfinityPickaxe;
import net.byAqua3.avaritia.item.ItemInfinityShovel;
import net.byAqua3.avaritia.item.ItemInfinitySingularity;
import net.byAqua3.avaritia.item.ItemInfinitySword;
import net.byAqua3.avaritia.item.ItemJsonSingularity;
import net.byAqua3.avaritia.item.ItemMatterCluster;
import net.byAqua3.avaritia.item.ItemNeutronNugget;
import net.byAqua3.avaritia.item.ItemNeutronPile;
import net.byAqua3.avaritia.item.ItemNeutroniumIngot;
import net.byAqua3.avaritia.item.ItemRecordFragment;
import net.byAqua3.avaritia.item.ItemSingularity;
import net.byAqua3.avaritia.item.ItemSkullFireSword;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;

import java.util.function.Function;

public class AvaritiaItems {

	public static final Rarity COSMIC_RARITY = AvaritiaEnumParams.COSMIC_RARITY_ENUM_PROXY.getValue();

	private static ResourceKey<Item> key(String path) {
		return ResourceKey.create(Registries.ITEM, Avaritia.id(path));
	}

	private static <T extends Item> T register(ResourceKey<Item> itemKey, Function<Item.Properties, T> factory, Item.Properties properties) {
		T item = factory.apply(properties.setId(itemKey));
		return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
	}

	public static final Item INFINITY_SWORD = register(key("infinity_sword"), ItemInfinitySword::new,
			new Item.Properties().rarity(COSMIC_RARITY).stacksTo(1).fireResistant().sword(AvaritiaToolMaterials.INFINITY, 2.0F, -2.4F));
	public static final Item SKULLFIRE_SWORD = register(key("skullfire_sword"), ItemSkullFireSword::new,
			new Item.Properties().rarity(Rarity.EPIC).sword(ToolMaterial.DIAMOND, 3.0F, -2.4F));
	public static final Item INFINITY_AXE = register(key("infinity_axe"), ItemInfinityAxe::new,
			new Item.Properties().rarity(COSMIC_RARITY).stacksTo(1).fireResistant().axe(AvaritiaToolMaterials.INFINITY, 29.0F, -3.0F));
	public static final Item INFINITY_PICKAXE = register(key("infinity_pickaxe"), ItemInfinityPickaxe::new,
			new Item.Properties().rarity(COSMIC_RARITY).stacksTo(1).fireResistant().pickaxe(AvaritiaToolMaterials.INFINITY, 15.0F, -2.8F));
	public static final Item INFINITY_SHOVEL = register(key("infinity_shovel"), ItemInfinityShovel::new,
			new Item.Properties().rarity(COSMIC_RARITY).stacksTo(1).fireResistant().shovel(AvaritiaToolMaterials.INFINITY, 16.0F, -2.0F));
	public static final Item INFINITY_HOE = register(key("infinity_hoe"), ItemInfinityHoe::new,
			new Item.Properties().rarity(COSMIC_RARITY).stacksTo(1).fireResistant().hoe(AvaritiaToolMaterials.INFINITY, 29.0F, 0.0F));
	public static final Item INFINITY_BOW = register(key("infinity_bow"), ItemInfinityBow::new,
			new Item.Properties().rarity(COSMIC_RARITY).stacksTo(1).fireResistant());
	public static final Item INFINITY_HELMET = register(key("infinity_helmet"),
			p -> new ItemInfinityArmor(net.minecraft.world.entity.EquipmentSlot.HEAD, p),
			infinityArmor(ArmorType.HELMET));
	public static final Item INFINITY_CHESTPLATE = register(key("infinity_chestplate"),
			p -> new ItemInfinityArmor(net.minecraft.world.entity.EquipmentSlot.CHEST, p),
			infinityArmor(ArmorType.CHESTPLATE));
	public static final Item INFINITY_LEGGINGS = register(key("infinity_leggings"),
			p -> new ItemInfinityArmor(net.minecraft.world.entity.EquipmentSlot.LEGS, p),
			infinityArmor(ArmorType.LEGGINGS));
	public static final Item INFINITY_BOOTS = register(key("infinity_boots"),
			p -> new ItemInfinityArmor(net.minecraft.world.entity.EquipmentSlot.FEET, p),
			infinityArmor(ArmorType.BOOTS));

	private static Item.Properties infinityArmor(ArmorType type) {
		Equippable equippable = Equippable.builder(type.getSlot())
				.setEquipSound(AvaritiaArmorMaterials.INFINITY.equipSound())
				.setAsset(AvaritiaArmorMaterials.INFINITY_ASSET)
				.setDamageOnHurt(false)
				.build();
		return new Item.Properties()
				.rarity(COSMIC_RARITY)
				.stacksTo(1)
				.fireResistant()
				.enchantable(1000)
				.attributes(AvaritiaArmorMaterials.INFINITY.createAttributes(type))
				.component(net.minecraft.core.component.DataComponents.EQUIPPABLE, equippable);
	}

	public static final Item RECORD_FRAGMENT = register(key("record_fragment"), ItemRecordFragment::new,
			new Item.Properties().rarity(COSMIC_RARITY));
	public static final Item DIAMOND_LATTICE = register(key("diamond_lattice"), Item::new,
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item CRYSTAL_MATRIX_INGOT = register(key("crystal_matrix_ingot"), ItemCrystalMatrixIngot::new,
			new Item.Properties().rarity(Rarity.RARE));
	public static final Item NEUTRON_PILE = register(key("neutron_pile"), ItemNeutronPile::new,
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item NEUTRON_NUGGET = register(key("neutron_nugget"), ItemNeutronNugget::new,
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item NEUTRONIUM_INGOT = register(key("neutronium_ingot"), ItemNeutroniumIngot::new,
			new Item.Properties().rarity(Rarity.RARE));
	public static final Item INFINITY_CATALYST = register(key("infinity_catalyst"), ItemInfinityCatalyst::new,
			new Item.Properties().rarity(Rarity.EPIC));
	public static final Item INFINITY_INGOT = register(key("infinity_ingot"), ItemInfinityIngot::new,
			new Item.Properties().rarity(COSMIC_RARITY));

	public static final Item ENDEST_PEARL = register(key("endest_pearl"), ItemEndestPearl::new,
			new Item.Properties().rarity(Rarity.RARE));
	public static final Item MATTER_CLUSTER = register(key("matter_cluster"), ItemMatterCluster::new,
			new Item.Properties().rarity(COSMIC_RARITY).stacksTo(1));

	public static final Item COPPER_SINGULARITY = register(key("copper_singularity"), p -> new ItemSingularity(p, 8999194, 14971392),
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item IRON_SINGULARITY = register(key("iron_singularity"), p -> new ItemSingularity(p, 8355711, 15132648),
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item GOLD_SINGULARITY = register(key("gold_singularity"), p -> new ItemSingularity(p, 14393875, 15265571),
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item LAPIS_SINGULARITY = register(key("lapis_singularity"), p -> new ItemSingularity(p, 2247599, 5931746),
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item REDSTONE_SINGULARITY = register(key("redstone_singularity"), p -> new ItemSingularity(p, 9437184, 14614528),
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item QUARTZ_SINGULARITY = register(key("quartz_singularity"), p -> new ItemSingularity(p, 9733757, 16777215),
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item DIAMOND_SINGULARITY = register(key("diamond_singularity"), p -> new ItemSingularity(p, 9424329, 4566181),
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item EMERALD_SINGULARITY = register(key("emerald_singularity"), p -> new ItemSingularity(p, 9228656, 6078004),
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item AMETHYST_SINGULARITY = register(key("amethyst_singularity"), p -> new ItemSingularity(p, 5519754, 11767539),
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item NETHERITE_SINGULARITY = register(key("netherite_singularity"), p -> new ItemSingularity(p, 3221802, 6637376),
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item JSON_SINGULARITY = register(key("json_singularity"), ItemJsonSingularity::new,
			new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item INFINITY_SINGULARITY = register(key("infinity_singularity"), ItemInfinitySingularity::new,
			new Item.Properties().rarity(Rarity.EPIC));

	public static final Item ULTIMATE_STEW = register(key("ultimate_stew"), Item::new,
			new Item.Properties().food(new FoodProperties(20, 20.0F, true)));
	public static final Item COSMIC_MEATBALLS = register(key("cosmic_meatballs"), Item::new,
			new Item.Properties().food(new FoodProperties(20, 20.0F, true)));

	public static void registerItems() {
	}
}


