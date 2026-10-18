package com.github._0x00acab.pings.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PingTargetingTest {

    @AfterEach
    void resetToDefault() {
        PingTargeting.resetMaxDistance();
    }

    @Test
    void defaultsToTheVanillaFallback() {
        assertEquals(PingTargeting.DEFAULT_MAX_DISTANCE, PingTargeting.maxDistance());
    }

    @Test
    void syncedDistanceReplacesTheDefault() {
        PingTargeting.setMaxDistance(96.0);
        assertEquals(96.0, PingTargeting.maxDistance());
    }

    @Test
    void resetRevertsToTheDefault() {
        PingTargeting.setMaxDistance(96.0);
        PingTargeting.resetMaxDistance();
        assertEquals(PingTargeting.DEFAULT_MAX_DISTANCE, PingTargeting.maxDistance());
    }
}
