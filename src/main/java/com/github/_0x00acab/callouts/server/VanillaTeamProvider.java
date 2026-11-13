package com.github._0x00acab.callouts.server;

import net.minecraft.server.level.ServerPlayer;

public final class VanillaTeamProvider implements TeamProvider {
	@Override
	public TeamId getTeam(ServerPlayer player) {
		var team = player.getTeam();
		return team == null ? null : new TeamId(team.getName());
	}
}
