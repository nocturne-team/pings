package com.github._0x00acab.pings.client;

import java.util.Locale;

public final class MarkerMath {
    public record Placement(double x, double y, boolean onScreen, double arrowAngle) {}

    private MarkerMath() {}

    public static Placement place(double ndcX, double ndcY, boolean inFront, int width, int height, int margin) {
        double screenX = (ndcX + 1) / 2 * width;
        double screenY = (1 - ndcY) / 2 * height;
        if (inFront && screenX >= margin && screenX <= width - margin && screenY >= margin && screenY <= height - margin) {
            return new Placement(screenX, screenY, true, 0);
        }

        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double dx = screenX - centerX;
        double dy = screenY - centerY;
        // Points behind the camera project mirrored through the center.
        if (!inFront) {
            dx = -dx;
            dy = -dy;
        }
        if (dx == 0 && dy == 0) {
            dy = 1;
        }
        double scale = Math.min((centerX - margin) / Math.abs(dx), (centerY - margin) / Math.abs(dy));
        return new Placement(centerX + dx * scale, centerY + dy * scale, false, Math.atan2(dy, dx));
    }

    public static String formatDistance(double blocks) {
        return blocks < 10 ? String.format(Locale.ROOT, "%.1fm", blocks) : Math.round(blocks) + "m";
    }
}
