package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.Avaritia;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class AvaritiaSounds {

	public static final SoundEvent GAPING_VOID = Registry.register(
			BuiltInRegistries.SOUND_EVENT,
			Avaritia.id("gaping_void"),
			SoundEvent.createVariableRangeEvent(Avaritia.id("gaping_void")));

	public static void registerSounds() {
	}
}
