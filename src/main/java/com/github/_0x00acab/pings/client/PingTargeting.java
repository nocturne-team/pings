package com.github._0x00acab.pings.client;

import com.github._0x00acab.pings.PingCategory;
import com.github._0x00acab.pings.network.PingRequestPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class PingTargeting {
    public static final double MAX_DISTANCE = 256.0;

    private PingTargeting() {}

    public static PingRequestPayload target(Minecraft minecraft, PingCategory category) {
        Entity camera = minecraft.getCameraEntity();
        if (camera == null || minecraft.level == null) {
            return null;
        }
        float partial = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        HitResult blockHit = camera.pick(MAX_DISTANCE, partial, false);
        Vec3 eye = camera.getEyePosition(partial);
        Vec3 look = camera.getViewVector(partial);
        double reach = blockHit.getType() == HitResult.Type.MISS ? MAX_DISTANCE : blockHit.getLocation().distanceTo(eye);

        Vec3 end = eye.add(look.scale(reach));
        AABB sweep = camera.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(camera, eye, end, sweep,
            entity -> !entity.isSpectator() && entity.isPickable(), reach * reach);

        if (entityHit != null) {
            Entity entity = entityHit.getEntity();
            return new PingRequestPayload(entity.position(), entity.getId(), category.ordinal());
        }
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            return new PingRequestPayload(blockHit.getLocation(), PingRequestPayload.NO_ENTITY, category.ordinal());
        }
        return null;
    }
}
