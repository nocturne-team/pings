package com.github._0x00acab.pings.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkerMathTest {
    private static final int W = 400;
    private static final int H = 200;
    private static final int MARGIN = 12;
    private static final double EPS = 1e-9;

    @Test
    void centeredPingInFrontIsOnScreenAtCenter() {
        MarkerMath.Placement p = MarkerMath.place(0, 0, 1, W, H, MARGIN);
        assertTrue(p.onScreen());
        assertEquals(200, p.x(), EPS);
        assertEquals(100, p.y(), EPS);
    }

    @Test
    void clipCoordinatesAreDividedByWAndYIsFlipped() {
        MarkerMath.Placement p = MarkerMath.place(1, 1, 2, W, H, MARGIN);
        assertTrue(p.onScreen());
        assertEquals(300, p.x(), EPS);
        assertEquals(50, p.y(), EPS);
    }

    @Test
    void pingFarRightClampsToRightEdgePointingRight() {
        MarkerMath.Placement p = MarkerMath.place(3, 0, 1, W, H, MARGIN);
        assertFalse(p.onScreen());
        assertEquals(W - MARGIN, p.x(), EPS);
        assertEquals(100, p.y(), EPS);
        assertEquals(0, p.arrowAngle(), EPS);
    }

    @Test
    void pingInsideMarginCountsAsOffScreen() {
        assertFalse(MarkerMath.place(0.99, 0, 1, W, H, MARGIN).onScreen());
    }

    @Test
    void pingDirectlyBehindPointsDownAtBottomEdge() {
        MarkerMath.Placement p = MarkerMath.place(0, 0, -1, W, H, MARGIN);
        assertFalse(p.onScreen());
        assertEquals(200, p.x(), EPS);
        assertEquals(H - MARGIN, p.y(), EPS);
        assertEquals(Math.PI / 2, p.arrowAngle(), EPS);
    }

    @Test
    void pingAlmostDirectlyBehindStaysAtBottomInsteadOfFlippingSides() {
        MarkerMath.Placement left = MarkerMath.place(-0.01, 0, -1, W, H, MARGIN);
        MarkerMath.Placement right = MarkerMath.place(0.01, 0, -1, W, H, MARGIN);
        assertEquals(H - MARGIN, left.y(), EPS);
        assertEquals(H - MARGIN, right.y(), EPS);
    }

    @Test
    void pingBehindAndToTheRightPointsRight() {
        MarkerMath.Placement p = MarkerMath.place(0.5, 0, -1, W, H, MARGIN);
        assertFalse(p.onScreen());
        assertEquals(W - MARGIN, p.x(), EPS);
        assertEquals(0, p.arrowAngle(), EPS);
    }

    @Test
    void pingInTheCameraPlanePointsTowardItWithoutNaN() {
        MarkerMath.Placement p = MarkerMath.place(0, 5, 0, W, H, MARGIN);
        assertFalse(p.onScreen());
        assertEquals(200, p.x(), EPS);
        assertEquals(MARGIN, p.y(), EPS);
        assertEquals(-Math.PI / 2, p.arrowAngle(), EPS);
    }

    @Test
    void nonFiniteProjectionIsSkipped() {
        assertNull(MarkerMath.place(Double.NaN, 0, 1, W, H, MARGIN));
        assertNull(MarkerMath.place(0, 0, Double.POSITIVE_INFINITY, W, H, MARGIN));
    }

    @Test
    void markersShrinkWithDistanceDownToAFloor() {
        assertEquals(1.0, MarkerMath.markerScale(3), EPS);
        assertEquals(1.0, MarkerMath.markerScale(8), EPS);
        assertEquals(0.5, MarkerMath.markerScale(16), EPS);
        assertEquals(MarkerMath.MIN_SCALE, MarkerMath.markerScale(500), EPS);
    }

    @Test
    void distanceFormatting() {
        assertEquals("2.5m", MarkerMath.formatDistance(2.5));
        assertEquals("42m", MarkerMath.formatDistance(42.4));
        assertEquals("256m", MarkerMath.formatDistance(255.6));
    }
}
