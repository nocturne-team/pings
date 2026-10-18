package com.github._0x00acab.pings.client;

import com.github._0x00acab.pings.PingCategory;
import net.minecraft.world.phys.Vec3;

public final class ActivePing {
    private final int entityId;
    private final PingCategory category;
    private final int color;
    private final long expiresAtMillis;
    private Vec3 lastKnownPosition;

    public ActivePing(Vec3 position, int entityId, PingCategory category, int color, long expiresAtMillis) {
        this.lastKnownPosition = position;
        this.entityId = entityId;
        this.category = category;
        this.color = color;
        this.expiresAtMillis = expiresAtMillis;
    }

    public Vec3 renderPosition(Vec3 liveEntityAnchor) {
        if (liveEntityAnchor != null) {
            lastKnownPosition = liveEntityAnchor;
        }
        return lastKnownPosition;
    }

    public Vec3 position() {
        return lastKnownPosition;
    }

    public int entityId() {
        return entityId;
    }

    public PingCategory category() {
        return category;
    }

    public int color() {
        return color;
    }

    public long expiresAtMillis() {
        return expiresAtMillis;
    }
}
