package com.github._0x00acab.callouts.server;

import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
//? if >=1.21 {
import net.minecraft.util.ARGB;
//?} else {
/*import java.awt.Color;
*///?}

public final class SenderColor {
	private SenderColor() {}

	//? if >=1.21 {
	public static int of(ServerPlayer player) {
		return player.waypointIcon().cloneAndAssignStyle(player).color
			.map(ARGB::opaque)
			.orElseGet(() -> fallback(player.getUUID()));
	}

	// Mirrors the color vanilla's LocatorBar draws when a waypoint carries no explicit color.
	static int fallback(UUID uuid) {
		return ARGB.setBrightness(ARGB.color(255, uuid.hashCode()), 0.9f);
	}
	//?} else {
	/*// No waypoints on 1.20.1: use the team color, else the same UUID-derived color newer versions fall back to.
	public static int of(ServerPlayer player) {
		var team = player.getTeam();
		Integer teamColor = team == null ? null : team.getColor().getColor();
		return teamColor != null ? 0xFF000000 | teamColor : fallback(player.getUUID());
	}

	static int fallback(UUID uuid) {
		int rgb = uuid.hashCode();
		float[] hsb = Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, null);
		return 0xFF000000 | (Color.HSBtoRGB(hsb[0], hsb[1], 0.9f) & 0xFFFFFF);
	}
	*///?}
}
