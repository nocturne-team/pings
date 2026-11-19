package com.github._0x00acab.callouts.server;

public final class VanillaPingMath {
	private static final String[] ARROWS = {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖"};
	// Closer than this horizontally, the ping is above or below you and an arrow would spin around.
	static final double NEAR = 1.5;

	private VanillaPingMath() {}

	// Arrow pointing at (dx, dz) relative to where the player faces. Minecraft yaw 0 faces +Z and grows clockwise.
	public static String arrow(float playerYaw, double dx, double dz) {
		if (dx * dx + dz * dz < NEAR * NEAR) {
			return "•";
		}
		double targetYaw = Math.toDegrees(Math.atan2(-dx, dz));
		double relative = ((targetYaw - playerYaw) % 360 + 360) % 360;
		return ARROWS[(int) Math.round(relative / 45.0) % 8];
	}

	public static String distance(double blocks) {
		return Math.round(blocks) + "m";
	}
}
