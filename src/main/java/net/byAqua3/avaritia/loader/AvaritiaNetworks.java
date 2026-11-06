package net.byAqua3.avaritia.loader;

import net.byAqua3.avaritia.network.PacketSingularitySync;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class AvaritiaNetworks {

	public static void registerNetworks() {
		PayloadTypeRegistry.clientboundPlay().register(PacketSingularitySync.TYPE, PacketSingularitySync.STREAM_CODEC);
	}
}

