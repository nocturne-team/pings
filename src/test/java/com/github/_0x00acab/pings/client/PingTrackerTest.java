package com.github._0x00acab.pings.client;

import com.github._0x00acab.pings.PingCategory;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PingTrackerTest {
    private final long[] now = {0L};
    private final PingTracker tracker = new PingTracker(() -> now[0]);

    private void addAt(double x) {
        tracker.add(new Vec3(x, 64, 0), -1, PingCategory.PLAIN, 0xFFFFFFFF);
    }

    @Test
    void addedPingIsActiveImmediately() {
        addAt(1);
        assertEquals(1, tracker.active().size());
        assertEquals(new Vec3(1, 64, 0), tracker.active().getFirst().position());
    }

    @Test
    void pingExpiresAfterTenSeconds() {
        addAt(1);
        now[0] = PingTracker.LIFETIME_MILLIS - 1;
        assertEquals(1, tracker.active().size());
        now[0] = PingTracker.LIFETIME_MILLIS;
        assertTrue(tracker.active().isEmpty());
    }

    @Test
    void eachPingHasItsOwnLifetime() {
        addAt(1);
        now[0] = 5_000;
        addAt(2);
        now[0] = 10_000;
        assertEquals(1, tracker.active().size());
        assertEquals(2, tracker.active().getFirst().position().x);
    }

    @Test
    void oldestPingIsDroppedPastCap() {
        for (int i = 0; i <= PingTracker.MAX_ACTIVE; i++) {
            addAt(i);
        }
        assertEquals(PingTracker.MAX_ACTIVE, tracker.active().size());
        assertEquals(1, tracker.active().getFirst().position().x);
    }

    @Test
    void sameLevelKeepsPings() {
        Object level = new Object();
        tracker.ensureLevel(level);
        addAt(1);
        tracker.ensureLevel(level);
        assertEquals(1, tracker.active().size());
    }

    @Test
    void levelChangeClearsPings() {
        tracker.ensureLevel(new String("overworld"));
        addAt(1);
        tracker.ensureLevel(new String("overworld"));
        assertTrue(tracker.active().isEmpty());
    }

    @Test
    void disconnectClearsPings() {
        tracker.ensureLevel(new Object());
        addAt(1);
        tracker.ensureLevel(null);
        assertTrue(tracker.active().isEmpty());
    }
}
