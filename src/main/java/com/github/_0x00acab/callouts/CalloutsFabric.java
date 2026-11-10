package com.github._0x00acab.callouts;

//? if fabric {
import com.github._0x00acab.callouts.network.PingBroadcastPayload;
import com.github._0x00acab.callouts.network.PingConfigSyncPayload;
import com.github._0x00acab.callouts.network.PingRequestPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CalloutsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		CalloutsMod.init();
		PayloadTypeRegistry.serverboundPlay().register(PingRequestPayload.TYPE, PingRequestPayload.STREAM_CODEC);
		PayloadTypeRegistry.clientboundPlay().register(PingBroadcastPayload.TYPE, PingBroadcastPayload.STREAM_CODEC);
		PayloadTypeRegistry.clientboundPlay().register(PingConfigSyncPayload.TYPE, PingConfigSyncPayload.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PingRequestPayload.TYPE,
			(payload, context) -> CalloutsMod.requestHandler().handle(context.player(), payload));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
			ModLoaderAccess.INSTANCE.sendToPlayer(handler.getPlayer(), new PingConfigSyncPayload(CalloutsMod.config().maxPingDistance())));
	}
}
//?}
