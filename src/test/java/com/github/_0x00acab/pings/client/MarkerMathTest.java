package com.github._0x00acab.pings.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkerMathTest {
    private static final int W = 400;
    private static final int H = 200;
    private static final int MARGIN = 12;
    private static final double EPS = 1e-9;

    @Test
    void centeredPingInFrontIsOnScreenAtCenter() {
        MarkerMath.Placement p = MarkerMath.place(0, 0, true, W, H, MARGIN);
        assertTrue(p.onScreen());
        assertEquals(200, p.x(), EPS);
        assertEquals(100, p.y(), EPS);
    }

    @Test
    void ndcMapsToGuiCoordinatesWithYFlipped() {
        MarkerMath.Placement p = MarkerMath.place(0.5, 0.5, true, W, H, MARGIN);
        assertTrue(p.onScreen());
        assertEquals(300, p.x(), EPS);
        assertEquals(50, p.y(), EPS);
    }

    @Test
    void pingFarRightClampsToRightEdgePointingRight() {
        MarkerMath.Placement p = MarkerMath.place(3, 0, true, W, H, MARGIN);
        assertFalse(p.onScreen());
        assertEquals(W - MARGIN, p.x(), EPS);
        assertEquals(100, p.y(), EPS);
        assertEquals(0, p.arrowAngle(), EPS);
    }

    @Test
    void pingInsideMarginCountsAsOffScreen() {
        assertFalse(MarkerMath.place(0.99, 0, true, W, H, MARGIN).onScreen());
    }

    @Test
    void pingDirectlyBehindPointsDownAtBottomEdge() {
        MarkerMath.Placement p = MarkerMath.place(0, 0, false, W, H, MARGIN);
        assertFalse(p.onScreen());
        assertEquals(200, p.x(), EPS);
        assertEquals(H - MARGIN, p.y(), EPS);
        assertEquals(Math.PI / 2, p.arrowAngle(), EPS);
    }

    @Test
    void pingBehindAndToTheRightIsMirroredToTheLeft() {
        MarkerMath.Placement p = MarkerMath.place(0.5, 0, false, W, H, MARGIN);
        assertFalse(p.onScreen());
        assertEquals(MARGIN, p.x(), EPS);
        assertEquals(Math.PI, Math.abs(p.arrowAngle()), EPS);
    }

    @Test
    void distanceFormatting() {
        assertEquals("2.5m", MarkerMath.formatDistance(2.5));
        assertEquals("42m", MarkerMath.formatDistance(42.4));
        assertEquals("256m", MarkerMath.formatDistance(255.6));
    }
}
