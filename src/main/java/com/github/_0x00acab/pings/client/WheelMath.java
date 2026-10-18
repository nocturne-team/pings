package com.github._0x00acab.pings.client;

public final class WheelMath {
    private WheelMath() {}

    public static int sectorAt(double dx, double dy, int sectors, double deadZone) {
        if (dx * dx + dy * dy < deadZone * deadZone) {
            return -1;
        }
        double angle = Math.atan2(dx, -dy);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        double width = 2 * Math.PI / sectors;
        return (int) Math.floor((angle + width / 2) / width) % sectors;
    }

    public static double sectorCenterAngle(int index, int sectors) {
        return index * 2 * Math.PI / sectors;
    }
}
