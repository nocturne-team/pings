package com.github._0x00acab.pings;

//? if neoforge {
/*import com.github._0x00acab.pings.network.PingBroadcastPayload;
import com.github._0x00acab.pings.network.PingConfigSyncPayload;
import com.github._0x00acab.pings.network.PingRequestPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(PingsMod.MOD_ID)
public class PingsNeoForge {
    public PingsNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        PingsMod.init();
        modEventBus.addListener(PingsNeoForge::registerPayloads);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) ->
            ModLoaderAccess.INSTANCE.sendToPlayer((ServerPlayer) event.getEntity(),
                new PingConfigSyncPayload(PingsMod.config().maxPingDistance())));
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToServer(PingRequestPayload.TYPE, PingRequestPayload.STREAM_CODEC,
            (payload, context) -> PingsMod.requestHandler().handle((ServerPlayer) context.player(), payload));
        registrar.playToClient(PingBroadcastPayload.TYPE, PingBroadcastPayload.STREAM_CODEC);
        registrar.playToClient(PingConfigSyncPayload.TYPE, PingConfigSyncPayload.STREAM_CODEC);
    }
}
*///?}
