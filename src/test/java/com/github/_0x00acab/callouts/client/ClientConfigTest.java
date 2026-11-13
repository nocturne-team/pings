package com.github._0x00acab.callouts.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientConfigTest {

	@TempDir
	Path dir;

	@Test
	void missingFileIsCreatedWithDefaults() {
		Path file = dir.resolve("callouts-client.properties");

		ClientConfig config = ClientConfig.load(file);

		assertTrue(Files.exists(file));
		assertEquals(1.0, config.markerScale);
		assertEquals(PingSound.DEFAULT.id(), config.sound);
		assertFalse(config.wheelToggle);
	}

	@Test
	void savedValuesRoundTrip() {
		Path file = dir.resolve("callouts-client.properties");
		ClientConfig config = ClientConfig.load(file);
		config.markerScale = 1.5;
		config.markerOpacity = 0.6;
		config.showDistance = false;
		config.showOffscreenArrows = false;
		config.sound = CustomSounds.PREFIX + "My Horn.ogg";
		config.soundVolume = 0.25;
		config.wheelToggle = true;
		config.save();

		ClientConfig loaded = ClientConfig.load(file);

		assertEquals(1.5, loaded.markerScale);
		assertEquals(0.6, loaded.markerOpacity);
		assertFalse(loaded.showDistance);
		assertFalse(loaded.showOffscreenArrows);
		assertEquals(CustomSounds.PREFIX + "My Horn.ogg", loaded.sound);
		assertEquals(0.25, loaded.soundVolume);
		assertTrue(loaded.wheelToggle);
	}

	@Test
	void outOfRangeNumbersAreClamped() throws IOException {
		Path file = dir.resolve("callouts-client.properties");
		Files.writeString(file, "marker_scale=10\nmarker_opacity=0\nsound_volume=-1\n");

		ClientConfig config = ClientConfig.load(file);

		assertEquals(ClientConfig.MAX_MARKER_SCALE, config.markerScale);
		assertEquals(ClientConfig.MIN_MARKER_OPACITY, config.markerOpacity);
		assertEquals(0.0, config.soundVolume);
	}

	@Test
	void garbageFallsBackToDefaults() throws IOException {
		Path file = dir.resolve("callouts-client.properties");
		Files.writeString(file, "marker_scale=big\nshow_distance=maybe\nsound=airhorn\n");

		ClientConfig config = ClientConfig.load(file);

		assertEquals(1.0, config.markerScale);
		assertTrue(config.showDistance);
		assertEquals(PingSound.DEFAULT.id(), config.sound);
	}

	@Test
	void customFileNamesBecomeValidIdentifierPaths() {
		assertEquals("my_horn__2_", CustomSounds.slug("My Horn (2).ogg"));
		assertEquals("ping-1.loud", CustomSounds.slug("ping-1.loud.OGG"));
	}

	@Test
	void everySoundIdRoundTrips() {
		for (PingSound sound : PingSound.values()) {
			assertEquals(sound, PingSound.byId(sound.id()));
		}
	}
}
