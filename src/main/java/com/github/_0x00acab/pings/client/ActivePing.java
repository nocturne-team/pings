package com.github._0x00acab.pings.client;

import com.github._0x00acab.pings.PingCategory;
import net.minecraft.world.phys.Vec3;

public record ActivePing(Vec3 position, int entityId, PingCategory category, int color, long expiresAtMillis) {}
