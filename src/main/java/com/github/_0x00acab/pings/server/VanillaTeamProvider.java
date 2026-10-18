package com.github._0x00acab.pings.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

public final class VanillaTeamProvider implements TeamProvider {
    @Override
    public TeamId getTeam(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        return team == null ? null : new TeamId(team.getName());
    }
}
