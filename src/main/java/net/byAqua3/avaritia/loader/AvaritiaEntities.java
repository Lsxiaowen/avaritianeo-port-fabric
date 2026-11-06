package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.entity.EntityEndestPearl;
import net.byAqua3.avaritia.entity.EntityGapingVoid;
import net.byAqua3.avaritia.entity.EntityInfinityArrow;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class AvaritiaEntities {

	public static final EntityType<EntityInfinityArrow> INFINITY_ARROW = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			Avaritia.id("infinity_arrow"),
			EntityType.Builder.<EntityInfinityArrow>of(EntityInfinityArrow::new, MobCategory.MISC)
					.clientTrackingRange(4)
					.updateInterval(20)
					.sized(0.5F, 0.5F)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, Avaritia.id("infinity_arrow"))));

	public static final EntityType<EntityEndestPearl> ENDEST_PEARL = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			Avaritia.id("endest_pearl"),
			EntityType.Builder.<EntityEndestPearl>of(EntityEndestPearl::new, MobCategory.MISC)
					.clientTrackingRange(4)
					.updateInterval(10)
					.sized(0.25F, 0.25F)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, Avaritia.id("endest_pearl"))));

	public static final EntityType<EntityGapingVoid> GAPING_VOID = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			Avaritia.id("gaping_void"),
			EntityType.Builder.<EntityGapingVoid>of(EntityGapingVoid::new, MobCategory.MISC)
					.clientTrackingRange(16)
					.updateInterval(10)
					.sized(0.1F, 0.1F)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, Avaritia.id("gaping_void"))));

	public static void registerEntities() {
	}
}
