package com.github._0x00acab.pings;

//? if neoforge {
/*import com.github._0x00acab.pings.client.PingClient;
import com.github._0x00acab.pings.client.PingHud;
import com.github._0x00acab.pings.network.PingBroadcastPayload;
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

@Mod(value = PingsMod.MOD_ID, dist = Dist.CLIENT)
public class PingsNeoForgeClient {
    public PingsNeoForgeClient(IEventBus modEventBus) {
        PingClient.init(request -> ClientPacketDistributor.sendToServer(request));
        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            event.register(PingClient.QUICK_PING_KEY);
            event.register(PingClient.WHEEL_KEY);
        });
        modEventBus.addListener((RegisterClientPayloadHandlersEvent event) ->
            event.register(PingBroadcastPayload.TYPE, (payload, context) -> PingClient.receive(payload)));
        modEventBus.addListener((RegisterGuiLayersEvent event) ->
            event.registerBelow(VanillaGuiLayers.CHAT, PingsMod.id("markers"), PingHud::render));
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> PingClient.tick(Minecraft.getInstance()));
    }
}
*///?}
