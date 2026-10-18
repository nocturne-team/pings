package com.github._0x00acab.pings.client;

import com.github._0x00acab.pings.PingCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;
import net.minecraft.world.phys.Vec3;

public final class PingTracker {
    public static final long LIFETIME_MILLIS = 10_000L;
    public static final int MAX_ACTIVE = 64;

    private final LongSupplier clock;
    private final List<ActivePing> pings = new ArrayList<>();
    private Object level;

    public PingTracker(LongSupplier clockMillis) {
        this.clock = clockMillis;
    }

    // Identity, not equals: a reconnect or dimension change creates a new level object.
    public void ensureLevel(Object level) {
        if (level == null || this.level != level) {
            pings.clear();
            this.level = level;
        }
    }

    public void add(Vec3 position, int entityId, PingCategory category, int color) {
        if (pings.size() >= MAX_ACTIVE) {
            pings.removeFirst();
        }
        pings.add(new ActivePing(position, entityId, category, color, clock.getAsLong() + LIFETIME_MILLIS));
    }

    public List<ActivePing> active() {
        long now = clock.getAsLong();
        pings.removeIf(ping -> now >= ping.expiresAtMillis());
        return List.copyOf(pings);
    }
}
