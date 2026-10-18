package com.github._0x00acab.pings.server;

import java.util.UUID;
import net.minecraft.util.ARGB;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SenderColorTest {
    private static final UUID NOTCH = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
    private static final UUID JEB = UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6");

    @Test
    void fallbackIsOpaque() {
        assertEquals(255, ARGB.alpha(SenderColor.fallback(NOTCH)));
    }

    @Test
    void fallbackIsStablePerPlayer() {
        assertEquals(SenderColor.fallback(NOTCH), SenderColor.fallback(UUID.fromString(NOTCH.toString())));
    }

    @Test
    void fallbackDiffersBetweenPlayers() {
        assertNotEquals(SenderColor.fallback(NOTCH), SenderColor.fallback(JEB));
    }
}
