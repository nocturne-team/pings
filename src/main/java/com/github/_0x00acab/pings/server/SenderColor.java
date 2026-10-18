package com.github._0x00acab.pings.server;

import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ARGB;

public final class SenderColor {
    private SenderColor() {}

    public static int of(ServerPlayer player) {
        return player.waypointIcon().cloneAndAssignStyle(player).color
            .map(ARGB::opaque)
            .orElseGet(() -> fallback(player.getUUID()));
    }

    // Mirrors the color vanilla's LocatorBar draws when a waypoint carries no explicit color.
    static int fallback(UUID uuid) {
        return ARGB.setBrightness(ARGB.color(255, uuid.hashCode()), 0.9f);
    }
}
