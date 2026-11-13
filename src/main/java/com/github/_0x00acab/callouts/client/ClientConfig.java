package com.github._0x00acab.callouts.client;

import com.github._0x00acab.callouts.CalloutsMod;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

public final class ClientConfig {
	public static final double MIN_MARKER_SCALE = 0.5;
	public static final double MAX_MARKER_SCALE = 2.0;
	public static final double MIN_MARKER_OPACITY = 0.2;

	public double markerScale = 1.0;
	public double markerOpacity = 1.0;
	public boolean showDistance = true;
	public boolean showOffscreenArrows = true;
	// A built-in PingSound id, or CustomSounds.PREFIX + a file name in config/callouts/sounds.
	public String sound = PingSound.DEFAULT.id();
	public double soundVolume = 1.0;
	public boolean wheelToggle = false;

	private Path file;

	public static ClientConfig load(Path file) {
		ClientConfig config = new ClientConfig();
		config.file = file;
		if (!Files.exists(file)) {
			config.save();
			return config;
		}
		Properties props = new Properties();
		try (Reader reader = Files.newBufferedReader(file)) {
			props.load(reader);
		} catch (IOException e) {
			CalloutsMod.LOGGER.warn("Could not read {}, using defaults", file, e);
			return config;
		}
		config.markerScale = readDouble(props, "marker_scale", config.markerScale, MIN_MARKER_SCALE, MAX_MARKER_SCALE);
		config.markerOpacity = readDouble(props, "marker_opacity", config.markerOpacity, MIN_MARKER_OPACITY, 1.0);
		config.showDistance = readBoolean(props, "show_distance", config.showDistance);
		config.showOffscreenArrows = readBoolean(props, "show_offscreen_arrows", config.showOffscreenArrows);
		config.soundVolume = readDouble(props, "sound_volume", config.soundVolume, 0.0, 1.0);
		config.wheelToggle = readBoolean(props, "wheel_toggle", config.wheelToggle);
		String soundId = props.getProperty("sound");
		if (soundId != null) {
			soundId = soundId.trim();
			if (soundId.startsWith(CustomSounds.PREFIX) || PingSound.byId(soundId) != null) {
				config.sound = soundId;
			} else {
				CalloutsMod.LOGGER.warn("Ignoring unknown sound={} in callouts client config", soundId);
			}
		}
		return config;
	}

	public void save() {
		if (file == null) {
			return;
		}
		Properties props = new Properties();
		props.setProperty("marker_scale", Double.toString(markerScale));
		props.setProperty("marker_opacity", Double.toString(markerOpacity));
		props.setProperty("show_distance", Boolean.toString(showDistance));
		props.setProperty("show_offscreen_arrows", Boolean.toString(showOffscreenArrows));
		props.setProperty("sound", sound);
		props.setProperty("sound_volume", Double.toString(soundVolume));
		props.setProperty("wheel_toggle", Boolean.toString(wheelToggle));
		try {
			Files.createDirectories(file.getParent());
			try (Writer writer = Files.newBufferedWriter(file)) {
				props.store(writer, "Callouts client settings. Also editable in-game from the mod list.");
			}
		} catch (IOException e) {
			CalloutsMod.LOGGER.warn("Could not write {}", file, e);
		}
	}

	private static double readDouble(Properties props, String key, double fallback, double min, double max) {
		String raw = props.getProperty(key);
		if (raw == null) {
			return fallback;
		}
		try {
			double value = Double.parseDouble(raw.trim());
			if (Double.isFinite(value)) {
				return Math.clamp(value, min, max);
			}
		} catch (NumberFormatException ignored) {
		}
		CalloutsMod.LOGGER.warn("Ignoring invalid {}={} in callouts client config", key, raw);
		return fallback;
	}

	private static boolean readBoolean(Properties props, String key, boolean fallback) {
		String raw = props.getProperty(key);
		if (raw == null) {
			return fallback;
		}
		return switch (raw.trim().toLowerCase(Locale.ROOT)) {
			case "true" -> true;
			case "false" -> false;
			default -> {
				CalloutsMod.LOGGER.warn("Ignoring invalid {}={} in callouts client config", key, raw);
				yield fallback;
			}
		};
	}
}
