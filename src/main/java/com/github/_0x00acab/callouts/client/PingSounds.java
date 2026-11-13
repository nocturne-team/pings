package com.github._0x00acab.callouts.client;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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

	// Low bass note when a ping fails, if enabled.
	public static void error() {
		ClientConfig config = PingClient.config();
		if (config.errorSound && config.soundVolume > 0) {
			Minecraft.getInstance().getSoundManager().play(
				SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BASS.value(), 0.6f, (float) config.soundVolume));
		}
	}

	// Pitched instruments only: drums have no pitch and mob heads need a skull.
	public static List<NoteBlockInstrument> noteInstruments() {
		return Arrays.stream(NoteBlockInstrument.values())
			.filter(instrument -> instrument.isTunable() && !instrument.hasCustomSound())
			.toList();
	}

	public static NoteBlockInstrument instrument(String name) {
		for (NoteBlockInstrument instrument : noteInstruments()) {
			if (instrument.getSerializedName().equals(name)) {
				return instrument;
			}
		}
		return null;
	}

	public static Component instrumentName(NoteBlockInstrument instrument) {
		return Component.translatable("callouts.sound.note." + instrument.getSerializedName());
	}

	public static Component label(String id) {
		NoteSound note = NoteSound.parse(id);
		if (note != null) {
			NoteBlockInstrument instrument = instrument(note.instrument());
			if (instrument != null) {
				return Component.translatable("callouts.sound.note", instrumentName(instrument), note.noteName());
			}
		}
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
		NoteSound note = NoteSound.parse(id);
		if (note != null) {
			NoteBlockInstrument instrument = instrument(note.instrument());
			instance = instrument == null ? null : new SimpleSoundInstance(
				//? if >=1.21 {
				instrument.getSoundEvent().value().location(),
				//?} else {
				/*instrument.getSoundEvent().value().getLocation(),
				*///?}
				 SoundSource.PLAYERS, (float) volume, note.pitch(), RANDOM,
				false, 0, SoundInstance.Attenuation.NONE, x, y, z, relative);
		} else if (id.startsWith(CustomSounds.PREFIX)) {
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
