package com.github._0x00acab.pings;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;

public class PingsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PingsMod.LOGGER.info("Pings client loaded");
    }
}
//?}
