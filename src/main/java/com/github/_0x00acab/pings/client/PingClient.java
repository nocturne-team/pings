package com.github._0x00acab.pings.client;

import com.github._0x00acab.pings.PingCategory;
import com.github._0x00acab.pings.network.PingBroadcastPayload;
import com.github._0x00acab.pings.network.PingConfigSyncPayload;
import com.github._0x00acab.pings.network.PingRequestPayload;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class PingClient {
    public static final KeyMapping QUICK_PING_KEY = new KeyMapping("key.pings.quick_ping", InputConstants.KEY_Z, KeyMapping.Category.MULTIPLAYER);
    public static final KeyMapping WHEEL_KEY = new KeyMapping("key.pings.ping_wheel", InputConstants.KEY_R, KeyMapping.Category.MULTIPLAYER);
    public static final PingTracker TRACKER = new PingTracker(() -> System.nanoTime() / 1_000_000L);

    private static Consumer<PingRequestPayload> sender = request -> {};

    private PingClient() {}

    public static void init(Consumer<PingRequestPayload> serverSender) {
        sender = serverSender;
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
            // A tap shorter than a tick is already released here and would leave the wheel stuck open.
            if (minecraft.gui.screen() == null && WHEEL_KEY.isDown()) {
                minecraft.gui.setScreen(new PingWheelScreen(WHEEL_KEY));
                // Opening a screen releases every key; re-read held keys so walking continues.
                KeyMapping.setAll();
            }
        }
    }

    public static void sendPing(Minecraft minecraft, PingCategory category) {
        PingRequestPayload request = PingTargeting.target(minecraft, category);
        if (request != null) {
            sender.accept(request);
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
        }
    }
}
