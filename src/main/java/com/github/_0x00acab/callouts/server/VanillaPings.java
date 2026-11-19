package com.github._0x00acab.callouts.server;

import com.github._0x00acab.callouts.PingCategory;
import com.github._0x00acab.callouts.network.PingBroadcastPayload;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
//? if <1.21 {
/*import org.joml.Vector3f;
*///?}

// Shows pings to players without the mod, using only vanilla packets: action bar, dust column, sound, glow.
public final class VanillaPings {
	// Same as the modded client's marker lifetime.
	static final int LIFETIME_TICKS = 200;
	// The action bar stays up ~3s after the last send, so stop early to fade out when the ping ends.
	private static final int ACTION_BAR_TICKS = LIFETIME_TICKS - 60;
	private static final int REFRESH_TICKS = 10;
	private static final int PARTICLE_TICKS = 5;
	private static final double COLUMN_HEIGHT = 4.0;
	private static final double COLUMN_STEP = 0.25;
	// Entity's shared flags byte (id 0 since forever); bit 6 is "glowing".
	private static final EntityDataAccessor<Byte> FLAGS = new EntityDataAccessor<>(0, EntityDataSerializers.BYTE);
	private static final byte GLOWING = 0x40;
	// The sound plays this far toward the ping so it's audible at any range but keeps its direction.
	private static final double SOUND_DISTANCE = 4.0;

	private final Map<UUID, Ping> bySender = new HashMap<>();

	private static final class Ping {
		final String senderName;
		final Level level;
		final List<ServerPlayer> viewers;
		final Entity entity;
		final PingCategory category;
		final int color;
		Vec3 position;
		int age;

		Ping(ServerPlayer sender, List<ServerPlayer> viewers, Vec3 position, Entity entity, PingCategory category, int color) {
			this.senderName = sender.getScoreboardName();
			this.level = sender.level();
			this.viewers = viewers;
			this.position = position;
			this.entity = entity;
			this.category = category;
			this.color = color & 0xFFFFFF;
		}
	}

	// position is where the modded marker goes (above the head for entities); entity may be null.
	public void show(ServerPlayer sender, List<ServerPlayer> viewers, Vec3 position, Entity entity, PingCategory category, int color) {
		Ping old = bySender.remove(sender.getUUID());
		if (old != null) {
			end(old);
		}
		Ping ping = new Ping(sender, new ArrayList<>(viewers), position, entity, category, color);
		bySender.put(sender.getUUID(), ping);
		for (ServerPlayer viewer : ping.viewers) {
			playSound(viewer, ping);
		}
		update(ping);
	}

	public void tick() {
		Iterator<Ping> it = bySender.values().iterator();
		while (it.hasNext()) {
			Ping ping = it.next();
			ping.age++;
			ping.viewers.removeIf(ServerPlayer::hasDisconnected);
			if (ping.age >= LIFETIME_TICKS || ping.viewers.isEmpty()) {
				end(ping);
				it.remove();
			} else {
				update(ping);
			}
		}
	}

	public void clear() {
		bySender.values().forEach(VanillaPings::end);
		bySender.clear();
	}

	private static void update(Ping ping) {
		if (ping.entity != null && !ping.entity.isRemoved()) {
			ping.position = ping.entity.position().add(0, ping.entity.getBbHeight() + PingBroadcastPayload.ENTITY_MARKER_OFFSET, 0);
		}
		boolean refresh = ping.age % REFRESH_TICKS == 0;
		boolean particles = ping.age % PARTICLE_TICKS == 0;
		for (ServerPlayer viewer : ping.viewers) {
			if (viewer.level() != ping.level) {
				continue;
			}
			if (particles) {
				spawnColumn(viewer, ping);
			}
			if (refresh && ping.age < ACTION_BAR_TICKS) {
				viewer.connection.send(new ClientboundSystemChatPacket(actionBar(viewer, ping), true));
			}
			if (refresh && hasLiveEntity(ping, viewer)) {
				sendFlags(viewer, ping.entity, (byte) (ping.entity.getEntityData().get(FLAGS) | GLOWING));
			}
		}
	}

	// Restores the entity's real flags so the glow goes away.
	private static void end(Ping ping) {
		for (ServerPlayer viewer : ping.viewers) {
			if (hasLiveEntity(ping, viewer)) {
				sendFlags(viewer, ping.entity, ping.entity.getEntityData().get(FLAGS));
			}
		}
	}

	private static boolean hasLiveEntity(Ping ping, ServerPlayer viewer) {
		return ping.entity != null && !ping.entity.isRemoved() && ping.entity != viewer && !viewer.hasDisconnected();
	}

	private static void sendFlags(ServerPlayer viewer, Entity entity, byte flags) {
		viewer.connection.send(new ClientboundSetEntityDataPacket(entity.getId(), List.of(SynchedEntityData.DataValue.create(FLAGS, flags))));
	}

	private static Component actionBar(ServerPlayer viewer, Ping ping) {
		Vec3 delta = ping.position.subtract(viewer.position());
		String arrow = VanillaPingMath.arrow(viewer.getYRot(), delta.x, delta.z);
		String what = ping.category.englishName();
		MutableComponent text = Component.literal(arrow + " ")
			.append(Component.literal(ping.senderName).withStyle(style -> style.withColor(TextColor.fromRgb(ping.color))))
			.append(Component.literal(" · " + what));
		if (ping.entity != null) {
			text.append(Component.literal(": ")).append(ping.entity.getName());
		}
		return text.append(Component.literal(" · " + VanillaPingMath.distance(delta.length())));
	}

	private static void spawnColumn(ServerPlayer viewer, Ping ping) {
		//? if >=1.21 {
		DustParticleOptions dust = new DustParticleOptions(ping.color, 1.5f);
		//?} else {
		/*DustParticleOptions dust = new DustParticleOptions(new Vector3f(
			((ping.color >> 16) & 0xFF) / 255f, ((ping.color >> 8) & 0xFF) / 255f, (ping.color & 0xFF) / 255f), 1.5f);
		*///?}
		ServerLevel level = (ServerLevel) viewer.level();
		Vec3 base = ping.position;
		for (double y = 0; y <= COLUMN_HEIGHT; y += COLUMN_STEP) {
			// Long-distance flag on, so the column shows past the usual 32-block particle range.
			//? if >=1.21 {
			level.sendParticles(viewer, dust, true, true, base.x, base.y + y, base.z, 1, 0, 0, 0, 0);
			//?} else {
			/*level.sendParticles(viewer, dust, true, base.x, base.y + y, base.z, 1, 0, 0, 0, 0);
			*///?}
		}
	}

	private static void playSound(ServerPlayer viewer, Ping ping) {
		Vec3 eye = viewer.getEyePosition();
		Vec3 delta = ping.position.subtract(eye);
		double length = delta.length();
		Vec3 at = length > SOUND_DISTANCE ? eye.add(delta.scale(SOUND_DISTANCE / length)) : ping.position;
		viewer.connection.send(new ClientboundSoundPacket(SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS,
			at.x, at.y, at.z, 1.0f, 1.0f, viewer.getRandom().nextLong()));
	}
}
