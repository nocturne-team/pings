package com.github._0x00acab.pings;

//? if fabric {
import net.fabricmc.api.ModInitializer;

public class PingsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PingsMod.LOGGER.info("Pings loaded");
    }
}
//?}
