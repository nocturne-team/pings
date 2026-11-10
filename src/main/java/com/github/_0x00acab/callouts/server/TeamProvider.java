package com.github._0x00acab.callouts.server;

import net.minecraft.server.level.ServerPlayer;

public interface TeamProvider {
	// Returns null when the player is not on a team.
	TeamId getTeam(ServerPlayer player);
}
