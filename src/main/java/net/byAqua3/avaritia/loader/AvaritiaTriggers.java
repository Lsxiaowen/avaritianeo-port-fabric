package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.Avaritia;
import net.byAqua3.avaritia.trigger.TriggerAvaritiaRoot;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class AvaritiaTriggers {

	public static final TriggerAvaritiaRoot ROOT = Registry.register(
			BuiltInRegistries.TRIGGER_TYPES, Avaritia.id("root"), new TriggerAvaritiaRoot());

	public static void registerTriggers() {
	}
}
