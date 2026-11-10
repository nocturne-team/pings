package com.github._0x00acab.callouts;

//? if fabric {
import com.github._0x00acab.callouts.client.PingClient;
import com.github._0x00acab.callouts.client.PingHud;
import com.github._0x00acab.callouts.network.PingBroadcastPayload;
import com.github._0x00acab.callouts.network.PingConfigSyncPayload;
import com.github._0x00acab.callouts.network.PingRequestPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

public class CalloutsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		PingClient.init(request -> {
			if (ClientPlayNetworking.canSend(PingRequestPayload.TYPE)) {
				ClientPlayNetworking.send(request);
			}
		});
		KeyMappingHelper.registerKeyMapping(PingClient.QUICK_PING_KEY);
		KeyMappingHelper.registerKeyMapping(PingClient.WHEEL_KEY);
		ClientTickEvents.END_CLIENT_TICK.register(PingClient::tick);
		ClientPlayNetworking.registerGlobalReceiver(PingBroadcastPayload.TYPE, (payload, context) -> PingClient.receive(payload));
		ClientPlayNetworking.registerGlobalReceiver(PingConfigSyncPayload.TYPE, (payload, context) -> PingClient.receiveConfig(payload));
		HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR, CalloutsMod.id("markers"), PingHud::render);
	}
}
//?}
