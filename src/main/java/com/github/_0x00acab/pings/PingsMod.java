package com.github._0x00acab.pings;

import com.github._0x00acab.pings.server.PingRateLimiter;
import com.github._0x00acab.pings.server.PingRequestHandler;
import com.github._0x00acab.pings.server.VanillaTeamProvider;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PingsMod {
    public static final String MOD_ID = "pings";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String VERSION = /*$ mod_version*/ "0.1.0";
    public static final String MINECRAFT = /*$ minecraft*/ "26.3";

    private static PingsConfig config;
    private static PingRequestHandler requestHandler;

    private PingsMod() {}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        config = PingsConfig.load(ModLoaderAccess.INSTANCE.getConfigDir().resolve("pings.properties"));
        requestHandler = new PingRequestHandler(config, new PingRateLimiter(config, () -> System.nanoTime() / 1_000_000L), new VanillaTeamProvider());
        LOGGER.info("Pings loaded");
    }

    public static PingsConfig config() {
        return config;
    }

    public static PingRequestHandler requestHandler() {
        return requestHandler;
    }
}
