package com.github._0x00acab.callouts.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public final class PingSounds {
	private static final RandomSource RANDOM = RandomSource.create();

	private PingSounds() {}

	// No attenuation: pings can be far away, but placing the sound at the ping keeps its direction audible.
	public static void playAt(Vec3 position) {
		ClientConfig config = PingClient.config();
		play(config.sound, config.soundVolume, position.x, position.y, position.z, false);
	}

	public static void preview(String id, double volume) {
		play(id, volume, 0, 0, 0, true);
	}

	public static Component label(String id) {
		if (id.startsWith(CustomSounds.PREFIX)) {
			return Component.literal(CustomSounds.displayName(id.substring(CustomSounds.PREFIX.length())));
		}
		PingSound sound = PingSound.byId(id);
		return Component.translatable((sound == null ? PingSound.DEFAULT : sound).translationKey());
	}

	private static void play(String id, double volume, double x, double y, double z, boolean relative) {
		if (volume <= 0) {
			return;
		}
		SoundInstance instance;
		if (id.startsWith(CustomSounds.PREFIX)) {
			// A missing or oversized file is skipped silently; the picker only lists files that exist.
			instance = CustomSounds.instance(id.substring(CustomSounds.PREFIX.length()), (float) volume, x, y, z, relative);
		} else {
			PingSound sound = PingSound.byId(id);
			instance = sound == null || sound == PingSound.NONE ? null : new SimpleSoundInstance(
				sound.event(), SoundSource.PLAYERS, (float) volume, 1.0f, RANDOM,
				false, 0, SoundInstance.Attenuation.NONE, x, y, z, relative);
		}
		if (instance != null) {
			Minecraft.getInstance().getSoundManager().play(instance);
		}
	}
}
