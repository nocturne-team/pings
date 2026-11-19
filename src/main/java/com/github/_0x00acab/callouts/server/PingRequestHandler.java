package com.github._0x00acab.callouts.server;

import com.github._0x00acab.callouts.ModLoaderAccess;
import com.github._0x00acab.callouts.PingCategory;
import com.github._0x00acab.callouts.CalloutsConfig;
import com.github._0x00acab.callouts.network.PingBroadcastPayload;
import com.github._0x00acab.callouts.network.PingRejectedPayload;
import com.github._0x00acab.callouts.network.PingRequestPayload;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class PingRequestHandler {
	private final CalloutsConfig config;
	private final PingRateLimiter limiter;
	private final TeamProvider teams;
	private final VanillaPings vanillaPings;

	public PingRequestHandler(CalloutsConfig config, PingRateLimiter limiter, TeamProvider teams, VanillaPings vanillaPings) {
		this.config = config;
		this.limiter = limiter;
		this.teams = teams;
		this.vanillaPings = vanillaPings;
	}

	public void handle(ServerPlayer sender, PingRequestPayload request) {
		PingCategory category = PingCategory.byId(request.categoryId());
		if (category == null) {
			return;
		}

		Vec3 target = request.position();
		// Range is checked against what was aimed at, not the marker drawn above an entity's head.
		Vec3 aimed = target;
		int entityId = PingRequestPayload.NO_ENTITY;
		Entity pinged = null;
		if (request.entityId() != PingRequestPayload.NO_ENTITY) {
			Entity entity = sender.level().getEntity(request.entityId());
			if (entity != null && !entity.isRemoved() && !entity.isInvisibleTo(sender) && isTrackedBy(sender, entity)) {
				aimed = entity.position();
				target = aimed.add(0, entity.getBbHeight() + PingBroadcastPayload.ENTITY_MARKER_OFFSET, 0);
				entityId = entity.getId();
				pinged = entity;
			}
		}

		if (!isValidTarget(sender.getEyePosition(), aimed, config.maxPingDistance())) {
			reject(sender, PingRejectedPayload.TOO_FAR, config.maxPingDistance());
			return;
		}
		switch (limiter.tryPing(sender.getUUID())) {
			case COOLDOWN -> {
				reject(sender, PingRejectedPayload.COOLDOWN, 0);
				return;
			}
			case LOCKED_OUT -> {
				// Every attempt while locked out restarts the quiet timer, so say how long to stop for.
				reject(sender, PingRejectedPayload.LOCKED_OUT, config.lockoutQuietMillis() / 1000.0);
				return;
			}
			case ACCEPTED -> {
			}
		}

		int color = SenderColor.of(sender);
		PingBroadcastPayload broadcast = new PingBroadcastPayload(target, entityId, category.ordinal(), color);
		List<ServerPlayer> vanilla = new ArrayList<>();
		for (ServerPlayer recipient : recipients(sender)) {
			if (ModLoaderAccess.INSTANCE.hasCallouts(recipient)) {
				ModLoaderAccess.INSTANCE.sendToPlayer(recipient, broadcast);
			} else {
				vanilla.add(recipient);
			}
		}
		if (config.vanillaFallback() && !vanilla.isEmpty()) {
			vanillaPings.show(sender, vanilla, target, pinged, category, color);
		}
	}

	private static void reject(ServerPlayer sender, int reason, double value) {
		ModLoaderAccess.INSTANCE.sendToPlayer(sender, new PingRejectedPayload(reason, value));
	}

	// Only entities the server already sends to this player can be pinged; otherwise a modified client could
	// guess entity ids and get back positions of entities outside its tracking range.
	private static boolean isTrackedBy(ServerPlayer player, Entity entity) {
		//? if >=1.21 {
		boolean[] tracked = {false};
		player.level().getChunkSource().chunkMap.forEachEntityTrackedBy(player, seen -> tracked[0] |= seen == entity);
		return tracked[0];
		//?} else {
		/*// 1.20.1 keeps the tracker private, so repeat its range check: type range capped by view distance.
		int viewDistance = player.server.getPlayerList().getViewDistance();
		double range = Math.min(entity.getType().clientTrackingRange(), viewDistance) * 16.0;
		double dx = player.getX() - entity.getX();
		double dz = player.getZ() - entity.getZ();
		return dx * dx + dz * dz <= range * range && entity.broadcastToPlayer(player);
		*///?}
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
