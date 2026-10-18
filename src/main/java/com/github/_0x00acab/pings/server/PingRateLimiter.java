package com.github._0x00acab.pings.server;

import com.github._0x00acab.pings.PingsConfig;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongSupplier;

public final class PingRateLimiter {
    public enum Result { ACCEPTED, COOLDOWN, LOCKED_OUT }

    private final PingsConfig config;
    private final LongSupplier clock;
    private final Map<UUID, State> states = new HashMap<>();

    public PingRateLimiter(PingsConfig config, LongSupplier clockMillis) {
        this.config = config;
        this.clock = clockMillis;
    }

    public Result tryPing(UUID player) {
        long now = clock.getAsLong();
        forgetIdle(now);
        State state = states.computeIfAbsent(player, id -> new State());

        if (state.lockedOut && now - state.lastAttempt >= config.lockoutQuietMillis()) {
            state.lockedOut = false;
            state.attempts.clear();
        }
        state.lastAttempt = now;
        if (state.lockedOut) {
            return Result.LOCKED_OUT;
        }

        while (!state.attempts.isEmpty() && now - state.attempts.peekFirst() >= config.burstWindowMillis()) {
            state.attempts.pollFirst();
        }
        state.attempts.addLast(now);
        if (state.attempts.size() > config.burstLimit()) {
            state.lockedOut = true;
            return Result.LOCKED_OUT;
        }

        if (state.hasAccepted && now - state.lastAccepted < config.cooldownMillis()) {
            return Result.COOLDOWN;
        }
        state.hasAccepted = true;
        state.lastAccepted = now;
        return Result.ACCEPTED;
    }

    int trackedCount() {
        return states.size();
    }

    private void forgetIdle(long now) {
        long idleAfter = Math.max(config.cooldownMillis(), Math.max(config.burstWindowMillis(), config.lockoutQuietMillis()));
        states.values().removeIf(state -> now - state.lastAttempt >= idleAfter);
    }

    private static final class State {
        final ArrayDeque<Long> attempts = new ArrayDeque<>();
        boolean hasAccepted;
        long lastAccepted;
        long lastAttempt;
        boolean lockedOut;
    }
}
