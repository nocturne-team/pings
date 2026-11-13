package com.github._0x00acab.callouts;

//? if neoforge && <1.21 {
/*import com.github._0x00acab.callouts.network.LegacyNetwork;
import com.github._0x00acab.callouts.network.PingConfigSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

// 1.20.1 entrypoint. NeoForge 47.1 is still the Forge API, so this jar also runs on Forge 47.
@Mod(CalloutsMod.MOD_ID)
public class CalloutsLegacyNeoForge {
	public CalloutsLegacyNeoForge() {
		CalloutsMod.init();
		LegacyNetwork.register();
		MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) ->
			ModLoaderAccess.INSTANCE.sendToPlayer((ServerPlayer) event.getEntity(),
				new PingConfigSyncPayload(CalloutsMod.config().maxPingDistance())));
		if (FMLEnvironment.dist == Dist.CLIENT) {
			CalloutsLegacyNeoForgeClient.init(FMLJavaModLoadingContext.get().getModEventBus());
		}
	}
}
*///?}
