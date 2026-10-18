package com.github._0x00acab.pings.server;

import com.github._0x00acab.pings.PingsConfig;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static com.github._0x00acab.pings.server.PingRateLimiter.Result.ACCEPTED;
import static com.github._0x00acab.pings.server.PingRateLimiter.Result.COOLDOWN;
import static com.github._0x00acab.pings.server.PingRateLimiter.Result.LOCKED_OUT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PingRateLimiterTest {
    private static final UUID ALICE = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    private static final UUID BOB = UUID.fromString("00000000-0000-0000-0000-00000000000b");

    private final long[] now = {0L};
    private final PingRateLimiter limiter = new PingRateLimiter(PingsConfig.DEFAULTS, () -> now[0]);

    @Test
    void firstPingIsAccepted() {
        assertEquals(ACCEPTED, limiter.tryPing(ALICE));
    }

    @Test
    void secondPingInsideCooldownIsRejected() {
        limiter.tryPing(ALICE);
        now[0] = 999;
        assertEquals(COOLDOWN, limiter.tryPing(ALICE));
    }

    @Test
    void pingAfterCooldownIsAccepted() {
        limiter.tryPing(ALICE);
        now[0] = 1000;
        assertEquals(ACCEPTED, limiter.tryPing(ALICE));
    }

    @Test
    void playersHaveIndependentCooldowns() {
        limiter.tryPing(ALICE);
        assertEquals(ACCEPTED, limiter.tryPing(BOB));
    }

    @Test
    void fifthAttemptWithinWindowLocksOut() {
        for (int i = 0; i < 4; i++) {
            now[0] = i * 100L;
            limiter.tryPing(ALICE);
        }
        now[0] = 400;
        assertEquals(LOCKED_OUT, limiter.tryPing(ALICE));
    }

    @Test
    void steadyPingingAtCooldownPaceNeverLocksOut() {
        for (int i = 0; i < 20; i++) {
            now[0] = i * 1000L;
            assertEquals(ACCEPTED, limiter.tryPing(ALICE), "ping " + i);
        }
    }

    @Test
    void attemptsDuringLockoutExtendIt() {
        lockOutAlice();
        now[0] = 400 + 4000;
        assertEquals(LOCKED_OUT, limiter.tryPing(ALICE));
        now[0] = 400 + 4000 + 4999;
        assertEquals(LOCKED_OUT, limiter.tryPing(ALICE));
    }

    @Test
    void lockoutClearsAfterQuietPeriod() {
        lockOutAlice();
        now[0] = 400 + 5000;
        assertEquals(ACCEPTED, limiter.tryPing(ALICE));
    }

    @Test
    void idlePlayersAreForgotten() {
        limiter.tryPing(ALICE);
        now[0] = 60_000;
        limiter.tryPing(BOB);
        assertEquals(1, limiter.trackedCount());
    }

    private void lockOutAlice() {
        for (int i = 0; i <= 4; i++) {
            now[0] = i * 100L;
            limiter.tryPing(ALICE);
        }
    }
}
