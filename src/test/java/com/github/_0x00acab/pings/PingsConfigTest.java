package com.github._0x00acab.pings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PingsConfigTest {

    @TempDir
    Path dir;

    @Test
    void defaultsMatchSpec() {
        assertEquals(1000L, PingsConfig.DEFAULTS.cooldownMillis());
        assertEquals(4, PingsConfig.DEFAULTS.burstLimit());
        assertEquals(3000L, PingsConfig.DEFAULTS.burstWindowMillis());
        assertEquals(5000L, PingsConfig.DEFAULTS.lockoutQuietMillis());
        assertEquals(128.0, PingsConfig.DEFAULTS.noTeamRadius());
        assertEquals(256.0, PingsConfig.DEFAULTS.maxPingDistance());
    }

    @Test
    void missingFileIsCreatedWithDefaults() {
        Path file = dir.resolve("pings.properties");

        PingsConfig config = PingsConfig.load(file);

        assertEquals(PingsConfig.DEFAULTS, config);
        assertTrue(Files.exists(file));
        assertEquals(PingsConfig.DEFAULTS, PingsConfig.load(file));
    }

    @Test
    void valuesInFileOverrideDefaults() throws IOException {
        Path file = dir.resolve("pings.properties");
        Files.writeString(file, "cooldown_millis=250\nno_team_radius=64.5\n");

        PingsConfig config = PingsConfig.load(file);

        assertEquals(250L, config.cooldownMillis());
        assertEquals(64.5, config.noTeamRadius());
        assertEquals(PingsConfig.DEFAULTS.burstLimit(), config.burstLimit());
    }

    @Test
    void garbageAndOutOfRangeValuesFallBackPerKey() throws IOException {
        Path file = dir.resolve("pings.properties");
        Files.writeString(file, "cooldown_millis=soon\nburst_limit=0\nno_team_radius=-5\nmax_ping_distance=NaN\nburst_window_millis=2000\n");

        PingsConfig config = PingsConfig.load(file);

        assertEquals(PingsConfig.DEFAULTS.cooldownMillis(), config.cooldownMillis());
        assertEquals(PingsConfig.DEFAULTS.burstLimit(), config.burstLimit());
        assertEquals(PingsConfig.DEFAULTS.noTeamRadius(), config.noTeamRadius());
        assertEquals(PingsConfig.DEFAULTS.maxPingDistance(), config.maxPingDistance());
        assertEquals(2000L, config.burstWindowMillis());
    }
}
