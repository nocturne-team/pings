package com.github._0x00acab.pings;

//? if fabric {
import com.github._0x00acab.pings.client.PingClient;
import com.github._0x00acab.pings.network.PingBroadcastPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class PingsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PingClient.init(ClientPlayNetworking::send);
        KeyMappingHelper.registerKeyMapping(PingClient.QUICK_PING_KEY);
        KeyMappingHelper.registerKeyMapping(PingClient.WHEEL_KEY);
        ClientTickEvents.END_CLIENT_TICK.register(PingClient::tick);
        ClientPlayNetworking.registerGlobalReceiver(PingBroadcastPayload.TYPE, (payload, context) -> PingClient.receive(payload));
    }
}
//?}
