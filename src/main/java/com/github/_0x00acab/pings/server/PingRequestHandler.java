package com.github._0x00acab.pings.server;

import com.github._0x00acab.pings.ModLoaderAccess;
import com.github._0x00acab.pings.PingCategory;
import com.github._0x00acab.pings.PingsConfig;
import com.github._0x00acab.pings.network.PingBroadcastPayload;
import com.github._0x00acab.pings.network.PingRequestPayload;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class PingRequestHandler {
    private final PingsConfig config;
    private final PingRateLimiter limiter;
    private final TeamProvider teams;

    public PingRequestHandler(PingsConfig config, PingRateLimiter limiter, TeamProvider teams) {
        this.config = config;
        this.limiter = limiter;
        this.teams = teams;
    }

    public void handle(ServerPlayer sender, PingRequestPayload request) {
        PingCategory category = PingCategory.byId(request.categoryId());
        if (category == null) {
            return;
        }

        Vec3 target = request.position();
        int entityId = PingRequestPayload.NO_ENTITY;
        if (request.entityId() != PingRequestPayload.NO_ENTITY) {
            Entity entity = sender.level().getEntity(request.entityId());
            if (entity != null && !entity.isRemoved()) {
                target = entity.position().add(0, entity.getBbHeight() + PingBroadcastPayload.ENTITY_MARKER_OFFSET, 0);
                entityId = entity.getId();
            }
        }

        if (!isValidTarget(sender.getEyePosition(), target, config.maxPingDistance())) {
            return;
        }
        if (limiter.tryPing(sender.getUUID()) != PingRateLimiter.Result.ACCEPTED) {
            return;
        }

        PingBroadcastPayload broadcast = new PingBroadcastPayload(target, entityId, category.ordinal(), SenderColor.of(sender));
        for (ServerPlayer recipient : recipients(sender)) {
            ModLoaderAccess.INSTANCE.sendToPlayer(recipient, broadcast);
        }
    }

    static boolean isValidTarget(Vec3 eye, Vec3 target, double maxDistance) {
        return Double.isFinite(target.x) && Double.isFinite(target.y) && Double.isFinite(target.z)
            && eye.distanceToSqr(target) <= maxDistance * maxDistance;
    }

    private List<ServerPlayer> recipients(ServerPlayer sender) {
        List<RecipientResolver.Candidate<ServerPlayer>> online = sender.level().getServer().getPlayerList().getPlayers()
            .stream()
            .map(this::candidate)
            .toList();
        return RecipientResolver.resolve(candidate(sender), online, config.noTeamRadius());
    }

    private RecipientResolver.Candidate<ServerPlayer> candidate(ServerPlayer player) {
        return new RecipientResolver.Candidate<>(player, teams.getTeam(player), player.level().dimension().toString(), player.position());
    }
}
