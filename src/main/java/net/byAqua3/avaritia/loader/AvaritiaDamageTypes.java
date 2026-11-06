package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.Avaritia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class AvaritiaDamageTypes {

	public static final ResourceKey<DamageType> INFINITY = ResourceKey.create(Registries.DAMAGE_TYPE, Avaritia.id("infinity"));

	private AvaritiaDamageTypes() {
	}
}
