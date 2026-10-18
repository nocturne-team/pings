package com.github._0x00acab.pings.server;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PingRequestHandlerTest {
    private static final Vec3 EYE = new Vec3(0, 65.6, 0);

    @Test
    void targetWithinRangeIsValid() {
        assertTrue(PingRequestHandler.isValidTarget(EYE, new Vec3(100, 64, 100), 256));
    }

    @Test
    void targetExactlyAtMaxDistanceIsValid() {
        assertTrue(PingRequestHandler.isValidTarget(EYE, EYE.add(256, 0, 0), 256));
    }

    @Test
    void targetBeyondMaxDistanceIsRejected() {
        assertFalse(PingRequestHandler.isValidTarget(EYE, new Vec3(10_000, 64, 0), 256));
    }

    @Test
    void nonFiniteCoordinatesAreRejected() {
        assertFalse(PingRequestHandler.isValidTarget(EYE, new Vec3(Double.NaN, 64, 0), 256));
        assertFalse(PingRequestHandler.isValidTarget(EYE, new Vec3(0, Double.POSITIVE_INFINITY, 0), 256));
        assertFalse(PingRequestHandler.isValidTarget(EYE, new Vec3(0, 64, Double.NEGATIVE_INFINITY), 256));
    }
}
