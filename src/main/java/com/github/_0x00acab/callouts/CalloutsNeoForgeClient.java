package com.github._0x00acab.callouts;

//? if neoforge {
/*import com.github._0x00acab.callouts.client.PingClient;
import com.github._0x00acab.callouts.client.PingHud;
import com.github._0x00acab.callouts.network.PingBroadcastPayload;
import com.github._0x00acab.callouts.network.PingConfigSyncPayload;
import com.github._0x00acab.callouts.network.PingRequestPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = CalloutsMod.MOD_ID, dist = Dist.CLIENT)
public class CalloutsNeoForgeClient {
	public CalloutsNeoForgeClient(IEventBus modEventBus) {
		PingClient.init(request -> {
			var connection = Minecraft.getInstance().getConnection();
			if (connection != null && connection.hasChannel(PingRequestPayload.TYPE)) {
				ClientPacketDistributor.sendToServer(request);
			}
		});
		modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
			event.register(PingClient.QUICK_PING_KEY);
			event.register(PingClient.WHEEL_KEY);
		});
		modEventBus.addListener((RegisterClientPayloadHandlersEvent event) -> {
			event.register(PingBroadcastPayload.TYPE, (payload, context) -> PingClient.receive(payload));
			event.register(PingConfigSyncPayload.TYPE, (payload, context) -> PingClient.receiveConfig(payload));
		});
		modEventBus.addListener((RegisterGuiLayersEvent event) ->
			event.registerBelow(VanillaGuiLayers.CROSSHAIR, CalloutsMod.id("markers"), PingHud::render));
		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> PingClient.tick(Minecraft.getInstance()));
	}
}
*///?}
