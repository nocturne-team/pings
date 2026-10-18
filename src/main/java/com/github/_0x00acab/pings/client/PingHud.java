package com.github._0x00acab.pings.client;

import com.github._0x00acab.pings.PingCategory;
import com.github._0x00acab.pings.network.PingBroadcastPayload;
import com.github._0x00acab.pings.network.PingRequestPayload;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3x2fStack;
import org.joml.Vector3fc;

public final class PingHud {
    private static final int EDGE_MARGIN = 16;
    private static final int FILL_HALF = 9;
    private static final int OUTLINE_HALF = 12;
    private static final int ICON = 12;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private PingHud() {}

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Camera camera = minecraft.gameRenderer.mainCamera();
        if (level == null || !camera.isInitialized()) {
            return;
        }
        float partial = deltaTracker.getGameTimeDeltaPartialTick(true);
        Vec3 cameraPos = camera.position();
        Vector3fc forward = camera.forwardVector();

        for (ActivePing ping : PingClient.TRACKER.active()) {
            Vec3 target = ping.renderPosition(liveAnchor(level, ping, partial));
            Vec3 relative = target.subtract(cameraPos);
            boolean inFront = relative.x * forward.x() + relative.y * forward.y() + relative.z * forward.z() > 0;
            Vec3 ndc = minecraft.gameRenderer.projectPointToScreen(target);
            MarkerMath.Placement placement = MarkerMath.place(ndc.x, ndc.y, inFront ? 1 : -1, graphics.guiWidth(), graphics.guiHeight(), EDGE_MARGIN);
            String distance = MarkerMath.formatDistance(relative.length());
            if (placement.onScreen()) {
                drawDiamond(graphics, minecraft, placement, ping, distance);
            } else {
                drawArrow(graphics, minecraft, placement, ping.color(), distance);
            }
        }
    }

    private static Vec3 liveAnchor(ClientLevel level, ActivePing ping, float partial) {
        if (ping.entityId() == PingRequestPayload.NO_ENTITY) {
            return null;
        }
        Entity entity = level.getEntity(ping.entityId());
        if (entity == null || entity.isRemoved()) {
            return null;
        }
        return entity.getPosition(partial).add(0, entity.getBbHeight() + PingBroadcastPayload.ENTITY_MARKER_OFFSET, 0);
    }

    private static void drawDiamond(GuiGraphicsExtractor graphics, Minecraft minecraft, MarkerMath.Placement placement, ActivePing ping, String distance) {
        float x = (float) placement.x();
        float y = (float) placement.y();
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.rotate((float) (Math.PI / 4));
        frame(graphics, OUTLINE_HALF, ping.color());
        graphics.fill(-FILL_HALF, -FILL_HALF, FILL_HALF, FILL_HALF, ping.color());
        pose.popMatrix();

        int ix = Math.round(x);
        int iy = Math.round(y);
        if (ping.category() != PingCategory.PLAIN) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ping.category().sprite(), ix - ICON / 2, iy - ICON / 2, ICON, ICON);
        }
        int diamondTop = (int) Math.ceil(OUTLINE_HALF * Math.sqrt(2));
        graphics.centeredText(minecraft.font, distance, ix, iy - diamondTop - minecraft.font.lineHeight - 1, TEXT_COLOR);
    }

    private static void frame(GuiGraphicsExtractor graphics, int half, int color) {
        graphics.fill(-half, -half, half, -half + 1, color);
        graphics.fill(-half, half - 1, half, half, color);
        graphics.fill(-half, -half + 1, -half + 1, half - 1, color);
        graphics.fill(half - 1, -half + 1, half, half - 1, color);
    }

    private static void drawArrow(GuiGraphicsExtractor graphics, Minecraft minecraft, MarkerMath.Placement placement, int color, String distance) {
        float x = (float) placement.x();
        float y = (float) placement.y();
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.rotate((float) placement.arrowAngle());
        graphics.fill(-6, -1, 2, 1, color);
        graphics.fill(0, -4, 2, 4, color);
        graphics.fill(2, -3, 4, 3, color);
        graphics.fill(4, -2, 6, 2, color);
        graphics.fill(6, -1, 7, 1, color);
        pose.popMatrix();

        int textX = Math.round(x - (float) Math.cos(placement.arrowAngle()) * 20);
        int textY = Math.round(y - (float) Math.sin(placement.arrowAngle()) * 14) - minecraft.font.lineHeight / 2;
        graphics.centeredText(minecraft.font, distance, textX, textY, TEXT_COLOR);
    }
}
