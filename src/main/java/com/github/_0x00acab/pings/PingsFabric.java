package com.github._0x00acab.pings;

//? if fabric {
import com.github._0x00acab.pings.network.PingBroadcastPayload;
import com.github._0x00acab.pings.network.PingRequestPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PingsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PingsMod.init();
        PayloadTypeRegistry.serverboundPlay().register(PingRequestPayload.TYPE, PingRequestPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PingBroadcastPayload.TYPE, PingBroadcastPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PingRequestPayload.TYPE,
            (payload, context) -> PingsMod.requestHandler().handle(context.player(), payload));
    }
}
//?}
