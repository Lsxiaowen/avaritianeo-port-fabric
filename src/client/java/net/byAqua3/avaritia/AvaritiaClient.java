package net.byAqua3.avaritia;

import net.byAqua3.avaritia.loader.AvaritiaEntityRenderers;
import net.byAqua3.avaritia.loader.AvaritiaScreens;
import net.byAqua3.avaritia.loader.AvaritiaItemModels;
import net.byAqua3.avaritia.loader.AvaritiaRenderTypes;
import net.byAqua3.avaritia.loader.AvaritiaModels;
import net.byAqua3.avaritia.network.PacketSingularitySync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.byAqua3.avaritia.event.AvaritiaClientEvent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AvaritiaClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) ->
				AvaritiaClientEvent.applyInfinitySwordTooltip(stack, lines));
		AvaritiaRenderTypes.initialize();
		AvaritiaItemModels.register();
		AvaritiaModels.register();
		ClientPlayNetworking.registerGlobalReceiver(PacketSingularitySync.TYPE,
				(packet, context) -> context.client().execute(() -> PacketSingularitySync.apply(packet)));

		AvaritiaEntityRenderers.registerEntityRenderers();
		AvaritiaScreens.registerScreens();
	}
}
