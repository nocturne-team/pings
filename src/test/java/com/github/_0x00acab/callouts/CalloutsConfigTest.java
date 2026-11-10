package com.github._0x00acab.callouts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalloutsConfigTest {

	@TempDir
	Path dir;

	@Test
	void defaultsMatchSpec() {
		assertEquals(1000L, CalloutsConfig.DEFAULTS.cooldownMillis());
		assertEquals(4, CalloutsConfig.DEFAULTS.burstLimit());
		assertEquals(3000L, CalloutsConfig.DEFAULTS.burstWindowMillis());
		assertEquals(5000L, CalloutsConfig.DEFAULTS.lockoutQuietMillis());
		assertEquals(128.0, CalloutsConfig.DEFAULTS.noTeamRadius());
		assertEquals(256.0, CalloutsConfig.DEFAULTS.maxPingDistance());
	}

	@Test
	void missingFileIsCreatedWithDefaults() {
		Path file = dir.resolve("callouts.properties");

		CalloutsConfig config = CalloutsConfig.load(file);

		assertEquals(CalloutsConfig.DEFAULTS, config);
		assertTrue(Files.exists(file));
		assertEquals(CalloutsConfig.DEFAULTS, CalloutsConfig.load(file));
	}

	@Test
	void valuesInFileOverrideDefaults() throws IOException {
		Path file = dir.resolve("callouts.properties");
		Files.writeString(file, "cooldown_millis=250\nno_team_radius=64.5\n");

		CalloutsConfig config = CalloutsConfig.load(file);

		assertEquals(250L, config.cooldownMillis());
		assertEquals(64.5, config.noTeamRadius());
		assertEquals(CalloutsConfig.DEFAULTS.burstLimit(), config.burstLimit());
	}

	@Test
	void garbageAndOutOfRangeValuesFallBackPerKey() throws IOException {
		Path file = dir.resolve("callouts.properties");
		Files.writeString(file, "cooldown_millis=soon\nburst_limit=0\nno_team_radius=-5\nmax_ping_distance=NaN\nburst_window_millis=2000\n");

		CalloutsConfig config = CalloutsConfig.load(file);

		assertEquals(CalloutsConfig.DEFAULTS.cooldownMillis(), config.cooldownMillis());
		assertEquals(CalloutsConfig.DEFAULTS.burstLimit(), config.burstLimit());
		assertEquals(CalloutsConfig.DEFAULTS.noTeamRadius(), config.noTeamRadius());
		assertEquals(CalloutsConfig.DEFAULTS.maxPingDistance(), config.maxPingDistance());
		assertEquals(2000L, config.burstWindowMillis());
	}
}
