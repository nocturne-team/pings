package com.github._0x00acab.pings.client;

import com.github._0x00acab.pings.PingCategory;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActivePingTest {
    private final ActivePing ping = new ActivePing(new Vec3(1, 2, 3), 7, PingCategory.DANGER, 0xFFFF0000, 10_000);

    @Test
    void followsLiveEntityWhenAvailable() {
        assertEquals(new Vec3(9, 9, 9), ping.renderPosition(new Vec3(9, 9, 9)));
    }

    @Test
    void staysWhereTheEntityWasLastSeenAfterItDisappears() {
        ping.renderPosition(new Vec3(5, 2, 3));
        ping.renderPosition(new Vec3(9, 2, 3));

        assertEquals(new Vec3(9, 2, 3), ping.renderPosition(null));
    }

    @Test
    void usesPingTimePositionWhenEntityWasNeverSeen() {
        assertEquals(new Vec3(1, 2, 3), ping.renderPosition(null));
    }
}
