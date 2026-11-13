package com.github._0x00acab.callouts.client;

import com.github._0x00acab.callouts.CalloutsMod;
import com.github._0x00acab.callouts.ModLoaderAccess;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.JOrbisAudioStream;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Util;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.RandomSource;

// Player-supplied .ogg files from config/callouts/sounds, played without a resource pack.
public final class CustomSounds {
	public static final String PREFIX = "file:";
	private static final long MAX_BYTES = 2L * 1024 * 1024;

	private CustomSounds() {}

	public static Path directory() {
		return ModLoaderAccess.INSTANCE.getConfigDir().resolve("callouts").resolve("sounds");
	}

	// File names (with extension) of every usable .ogg, sorted. Creates the folder so "open folder" has something to open.
	public static List<String> list() {
		Path dir = directory();
		try {
			Files.createDirectories(dir);
			try (Stream<Path> files = Files.list(dir)) {
				return files
					.filter(Files::isRegularFile)
					.map(path -> path.getFileName().toString())
					.filter(name -> name.toLowerCase(Locale.ROOT).endsWith(".ogg"))
					.sorted(String.CASE_INSENSITIVE_ORDER)
					.toList();
			}
		} catch (IOException e) {
			CalloutsMod.LOGGER.warn("Could not list custom sounds in {}", dir, e);
			return List.of();
		}
	}

	public static String displayName(String fileName) {
		return fileName.length() > 4 ? fileName.substring(0, fileName.length() - 4) : fileName;
	}

	// Identifier paths only allow [a-z0-9/._-].
	static String slug(String fileName) {
		return displayName(fileName).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_");
	}

	public static SoundInstance instance(String fileName, float volume, double x, double y, double z, boolean relative) {
		Path dir = directory();
		Path file = dir.resolve(fileName).normalize();
		if (!dir.equals(file.getParent())) {
			return null;
		}
		long modified;
		try {
			if (!Files.isRegularFile(file) || Files.size(file) > MAX_BYTES) {
				return null;
			}
			modified = Files.getLastModifiedTime(file).toMillis();
		} catch (IOException e) {
			return null;
		}
		// The modified time is part of the key, so editing a file is picked up without a resource reload.
		Identifier id = CalloutsMod.id("custom/" + slug(fileName) + "_" + Long.toHexString(modified));
		return new Instance(id, file, volume, x, y, z, relative);
	}

	private static SoundBuffer decode(Path file) {
		try (InputStream in = Files.newInputStream(file); JOrbisAudioStream stream = new JOrbisAudioStream(in)) {
			return new SoundBuffer(stream.readAll(), stream.getFormat());
		} catch (IOException e) {
			CalloutsMod.LOGGER.warn("Could not decode custom ping sound {}", file, e);
			throw new CompletionException(e);
		}
	}

	private static final class Instance extends AbstractSoundInstance {
		private final Path file;

		Instance(Identifier id, Path file, float volume, double x, double y, double z, boolean relative) {
			super(id, SoundSource.PLAYERS, RandomSource.create());
			this.file = file;
			this.volume = volume;
			this.x = x;
			this.y = y;
			this.z = z;
			this.relative = relative;
			this.attenuation = SoundInstance.Attenuation.NONE;
		}

		// Builds the event in place of a sounds.json lookup, and puts the decoded file where the engine looks for it.
		@Override
		public WeighedSoundEvents getOrResolve(SoundManager manager) {
			sound = new Sound(identifier, ConstantFloat.of(1.0f), ConstantFloat.of(1.0f), 1, Sound.Type.FILE, false, false, 16);
			soundEvent = new WeighedSoundEvents(identifier, "subtitles.callouts.ping");
			soundEvent.addSound(sound);
			Minecraft.getInstance().getSoundManager().soundEngine.soundBuffers.cache
				.computeIfAbsent(sound.getPath(), key -> CompletableFuture.supplyAsync(() -> decode(file), Util.nonCriticalIoPool()));
			return soundEvent;
		}
	}
}
