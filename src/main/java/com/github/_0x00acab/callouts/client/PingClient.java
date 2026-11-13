package com.github._0x00acab.callouts.client;

import com.github._0x00acab.callouts.ModLoaderAccess;
import com.github._0x00acab.callouts.PingCategory;
import com.github._0x00acab.callouts.network.PingBroadcastPayload;
import com.github._0x00acab.callouts.network.PingConfigSyncPayload;
import com.github._0x00acab.callouts.network.PingRequestPayload;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Predicate;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class PingClient {
	public static final KeyMapping QUICK_PING_KEY = new KeyMapping("key.callouts.quick_ping", InputConstants.KEY_Z, KeyMapping.Category.MULTIPLAYER);
	public static final KeyMapping WHEEL_KEY = new KeyMapping("key.callouts.ping_wheel", InputConstants.KEY_R, KeyMapping.Category.MULTIPLAYER);
	public static final PingTracker TRACKER = new PingTracker(() -> System.nanoTime() / 1_000_000L);

	// Returns false when the server can't receive pings (it doesn't have the mod).
	private static Predicate<PingRequestPayload> sender = request -> false;
	private static ClientConfig config = new ClientConfig();

	private PingClient() {}

	public static void init(Predicate<PingRequestPayload> serverSender) {
		sender = serverSender;
		config = ClientConfig.load(ModLoaderAccess.INSTANCE.getConfigDir().resolve("callouts-client.properties"));
	}

	public static ClientConfig config() {
		return config;
	}

	public static void tick(Minecraft minecraft) {
		TRACKER.ensureLevel(minecraft.level);
		if (minecraft.level == null) {
			// Back at the title screen or between servers; drop any distance limit synced from the last one.
			PingTargeting.resetMaxDistance();
		}
		if (minecraft.player == null) {
			return;
		}
		while (QUICK_PING_KEY.consumeClick()) {
			sendPing(minecraft, PingCategory.PLAIN);
		}
		while (WHEEL_KEY.consumeClick()) {
			// In hold mode, a tap shorter than a tick is already released here and would leave the wheel stuck open.
			if (minecraft.gui.screen() == null && (config.wheelToggle || WHEEL_KEY.isDown())) {
				minecraft.gui.setScreen(new PingWheelScreen(WHEEL_KEY, config.wheelToggle));
				// Opening a screen releases every key; re-read held keys so walking continues.
				KeyMapping.setAll();
			}
		}
	}

	public static void sendPing(Minecraft minecraft, PingCategory category) {
		PingRequestPayload request = PingTargeting.target(minecraft, category);
		if (request == null) {
			minecraft.player.sendOverlayMessage(Component.translatable("callouts.feedback.nothing_to_ping", Math.round(PingTargeting.maxDistance())));
		} else if (!sender.test(request)) {
			minecraft.player.sendOverlayMessage(Component.translatable("callouts.feedback.server_missing_mod"));
		}
	}

	public static void receiveConfig(PingConfigSyncPayload payload) {
		PingTargeting.setMaxDistance(payload.maxPingDistance());
	}

	public static void receive(PingBroadcastPayload payload) {
		PingCategory category = PingCategory.byId(payload.categoryId());
		if (category != null) {
			TRACKER.ensureLevel(Minecraft.getInstance().level);
			TRACKER.add(payload.position(), payload.entityId(), category, payload.color());
			PingSounds.playAt(payload.position());
		}
	}
}
