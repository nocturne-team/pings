package com.github._0x00acab.pings.client;

import java.util.Locale;

public final class MarkerMath {
    public static final double MIN_SCALE = 0.4;
    private static final double FULL_SIZE_DISTANCE = 8.0;
    private static final double IN_FRONT_EPSILON = 1e-4;
    private static final double BEHIND_SNAP = 0.1;

    public record Placement(double x, double y, boolean onScreen, double arrowAngle) {}

    private MarkerMath() {}

    // Takes clip-space coordinates so targets behind or beside the camera never divide by w.
    public static Placement place(double clipX, double clipY, double clipW, int width, int height, int margin) {
        if (!Double.isFinite(clipX) || !Double.isFinite(clipY) || !Double.isFinite(clipW)) {
            return null;
        }
        boolean inFront = clipW > IN_FRONT_EPSILON;
        if (inFront) {
            double screenX = (clipX / clipW + 1) / 2 * width;
            double screenY = (1 - clipY / clipW) / 2 * height;
            if (screenX >= margin && screenX <= width - margin && screenY >= margin && screenY <= height - margin) {
                return new Placement(screenX, screenY, true, 0);
            }
        }

        double dx = clipX * width / 2.0;
        double dy = -clipY * height / 2.0;
        if (!inFront && Math.hypot(clipX, clipY) < BEHIND_SNAP * Math.abs(clipW)) {
            dx = 0;
            dy = 1;
        }
        if (dx == 0 && dy == 0) {
            dy = 1;
        }
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double scale = Math.min((centerX - margin) / Math.abs(dx), (centerY - margin) / Math.abs(dy));
        return new Placement(centerX + dx * scale, centerY + dy * scale, false, Math.atan2(dy, dx));
    }

    public static double markerScale(double distance) {
        return Math.max(MIN_SCALE, Math.min(1.0, FULL_SIZE_DISTANCE / distance));
    }

    public static String formatDistance(double blocks) {
        return blocks < 10 ? String.format(Locale.ROOT, "%.1fm", blocks) : Math.round(blocks) + "m";
    }
}
