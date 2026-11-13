package com.github._0x00acab.callouts;

//? if neoforge && <1.21 {
/*import com.github._0x00acab.callouts.client.CalloutsConfigScreen;
import com.github._0x00acab.callouts.client.PingClient;
import com.github._0x00acab.callouts.client.PingHud;
import com.github._0x00acab.callouts.network.LegacyNetwork;
import com.github._0x00acab.callouts.network.PingBroadcastPayload;
import com.github._0x00acab.callouts.network.PingConfigSyncPayload;
import com.github._0x00acab.callouts.network.PingRejectedPayload;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;

final class CalloutsLegacyNeoForgeClient {
	private CalloutsLegacyNeoForgeClient() {}

	static void init(IEventBus modEventBus) {
		PingClient.init(request -> {
			var connection = Minecraft.getInstance().getConnection();
			if (connection == null || !LegacyNetwork.isPresent(connection.getConnection())) {
				return false;
			}
			LegacyNetwork.CHANNEL.sendToServer(request);
			return true;
		});
		LegacyNetwork.clientReceiver = payload -> {
			if (payload instanceof PingBroadcastPayload broadcast) {
				PingClient.receive(broadcast);
			} else if (payload instanceof PingConfigSyncPayload sync) {
				PingClient.receiveConfig(sync);
			} else if (payload instanceof PingRejectedPayload rejected) {
				PingClient.receiveRejection(rejected);
			}
		};
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
			() -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new CalloutsConfigScreen(parent)));
		modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
			event.register(PingClient.QUICK_PING_KEY);
			event.register(PingClient.WHEEL_KEY);
		});
		modEventBus.addListener((RegisterGuiOverlaysEvent event) ->
			event.registerBelow(VanillaGuiOverlay.CROSSHAIR.id(), "markers",
				(gui, graphics, partialTick, width, height) -> PingHud.render(graphics, partialTick)));
		// The level pass is where 1.20.1 exposes the bobbed projection and camera rotation the HUD projects with.
		MinecraftForge.EVENT_BUS.addListener((RenderLevelStageEvent event) -> {
			if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
				PingHud.captureCamera(event.getProjectionMatrix(), event.getPoseStack(), event.getCamera());
			}
		});
		MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
			if (event.phase == TickEvent.Phase.END) {
				PingClient.tick(Minecraft.getInstance());
			}
		});
	}
}
*///?}
