package com.github._0x00acab.pings.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WheelMathTest {
    private static final int SECTORS = 5;
    private static final double DEAD_ZONE = 15;

    private static double[] at(double degreesClockwiseFromUp, double distance) {
        double rad = Math.toRadians(degreesClockwiseFromUp);
        return new double[] {Math.sin(rad) * distance, -Math.cos(rad) * distance};
    }

    private static int sector(double degrees) {
        double[] d = at(degrees, 50);
        return WheelMath.sectorAt(d[0], d[1], SECTORS, DEAD_ZONE);
    }

    @Test
    void insideDeadZoneSelectsNothing() {
        assertEquals(-1, WheelMath.sectorAt(0, 0, SECTORS, DEAD_ZONE));
        assertEquals(-1, WheelMath.sectorAt(10, -10, SECTORS, DEAD_ZONE));
    }

    @Test
    void straightUpIsFirstSector() {
        assertEquals(0, sector(0));
    }

    @Test
    void sectorsRunClockwise() {
        assertEquals(1, sector(72));
        assertEquals(2, sector(144));
        assertEquals(3, sector(216));
        assertEquals(4, sector(288));
    }

    @Test
    void justLeftOfUpWrapsToFirstSector() {
        assertEquals(0, sector(350));
    }

    @Test
    void sectorCentersMatchSelection() {
        for (int i = 0; i < SECTORS; i++) {
            double degrees = Math.toDegrees(WheelMath.sectorCenterAngle(i, SECTORS));
            assertEquals(i, sector(degrees));
        }
    }
}
