package com.github._0x00acab.callouts;

import com.github._0x00acab.callouts.server.PingRateLimiter;
import com.github._0x00acab.callouts.server.PingRequestHandler;
import com.github._0x00acab.callouts.server.VanillaTeamProvider;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CalloutsMod {
	public static final String MOD_ID = "callouts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String VERSION = /*$ mod_version*/ "0.1.0";
	public static final String MINECRAFT = /*$ minecraft*/ "26.3";

	private static CalloutsConfig config;
	private static PingRequestHandler requestHandler;

	private CalloutsMod() {}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void init() {
		config = CalloutsConfig.load(ModLoaderAccess.INSTANCE.getConfigDir().resolve("callouts.properties"));
		requestHandler = new PingRequestHandler(config, new PingRateLimiter(config, () -> System.nanoTime() / 1_000_000L), new VanillaTeamProvider());
		LOGGER.info("Callouts loaded");
	}

	public static CalloutsConfig config() {
		return config;
	}

	public static PingRequestHandler requestHandler() {
		return requestHandler;
	}
}
