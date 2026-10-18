package com.github._0x00acab.pings;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public record PingsConfig(
    long cooldownMillis,
    int burstLimit,
    long burstWindowMillis,
    long lockoutQuietMillis,
    double noTeamRadius,
    double maxPingDistance
) {
    public static final PingsConfig DEFAULTS = new PingsConfig(1000L, 4, 3000L, 5000L, 128.0, 256.0);

    public static PingsConfig load(Path file) {
        if (!Files.exists(file)) {
            save(file, DEFAULTS);
            return DEFAULTS;
        }
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            props.load(reader);
        } catch (IOException e) {
            PingsMod.LOGGER.warn("Could not read {}, using defaults", file, e);
            return DEFAULTS;
        }
        return new PingsConfig(
            readLong(props, "cooldown_millis", DEFAULTS.cooldownMillis, 0),
            (int) readLong(props, "burst_limit", DEFAULTS.burstLimit, 1),
            readLong(props, "burst_window_millis", DEFAULTS.burstWindowMillis, 0),
            readLong(props, "lockout_quiet_millis", DEFAULTS.lockoutQuietMillis, 0),
            readPositiveDouble(props, "no_team_radius", DEFAULTS.noTeamRadius),
            readPositiveDouble(props, "max_ping_distance", DEFAULTS.maxPingDistance));
    }

    private static void save(Path file, PingsConfig config) {
        Properties props = new Properties();
        props.setProperty("cooldown_millis", Long.toString(config.cooldownMillis));
        props.setProperty("burst_limit", Integer.toString(config.burstLimit));
        props.setProperty("burst_window_millis", Long.toString(config.burstWindowMillis));
        props.setProperty("lockout_quiet_millis", Long.toString(config.lockoutQuietMillis));
        props.setProperty("no_team_radius", Double.toString(config.noTeamRadius));
        props.setProperty("max_ping_distance", Double.toString(config.maxPingDistance));
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                props.store(writer, "Pings server settings. Delete a line to restore its default.");
            }
        } catch (IOException e) {
            PingsMod.LOGGER.warn("Could not write default config to {}", file, e);
        }
    }

    private static long readLong(Properties props, String key, long fallback, long min) {
        String raw = props.getProperty(key);
        if (raw == null) {
            return fallback;
        }
        try {
            long value = Long.parseLong(raw.trim());
            if (value >= min) {
                return value;
            }
        } catch (NumberFormatException ignored) {
        }
        PingsMod.LOGGER.warn("Ignoring invalid {}={} in pings config", key, raw);
        return fallback;
    }

    private static double readPositiveDouble(Properties props, String key, double fallback) {
        String raw = props.getProperty(key);
        if (raw == null) {
            return fallback;
        }
        try {
            double value = Double.parseDouble(raw.trim());
            if (Double.isFinite(value) && value > 0) {
                return value;
            }
        } catch (NumberFormatException ignored) {
        }
        PingsMod.LOGGER.warn("Ignoring invalid {}={} in pings config", key, raw);
        return fallback;
    }
}
