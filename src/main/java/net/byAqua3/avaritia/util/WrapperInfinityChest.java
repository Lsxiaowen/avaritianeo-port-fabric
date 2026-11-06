package net.byAqua3.avaritia.util;

import net.minecraft.world.Container;

/**
 * Marker wrapper kept for compatibility; Fabric's ContainerStorage handles
 * item transfer for any Container, so no custom handler is required.
 */
public class WrapperInfinityChest {

	private final Container inventory;

	public WrapperInfinityChest(Container inventory) {
		this.inventory = inventory;
	}

	public Container getInventory() {
		return this.inventory;
	}
}
