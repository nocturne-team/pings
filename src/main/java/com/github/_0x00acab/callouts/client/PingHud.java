package com.github._0x00acab.callouts.client;

import com.github._0x00acab.callouts.PingCategory;
import com.github._0x00acab.callouts.network.PingBroadcastPayload;
import com.github._0x00acab.callouts.network.PingRequestPayload;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class PingHud {
	private static final int EDGE_MARGIN = 16;
	private static final int FILL_HALF = 9;
	private static final int OUTLINE_HALF = 12;
	private static final int ICON = 13;
	private static final int TEXT_COLOR = 0xFFFFFFFF;

	private PingHud() {}

	public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		PingClient.TRACKER.ensureLevel(level);
		if (level == null) {
			return;
		}
		GameRenderState state = minecraft.gameRenderer.gameRenderState();
		CameraRenderState camera = state.levelRenderState.cameraRenderState;
		if (camera.pos == null || camera.projectionMatrix == null || camera.viewRotationMatrix == null) {
			return;
		}
		Matrix4f worldToClip = worldToClip(minecraft.gameRenderer, state, camera);
		float partial = deltaTracker.getGameTimeDeltaPartialTick(true);
		ClientConfig config = PingClient.config();
		int textColor = MarkerMath.withOpacity(TEXT_COLOR, config.markerOpacity);

		for (ActivePing ping : PingClient.TRACKER.active()) {
			Vec3 target = ping.renderPosition(liveAnchor(level, ping, partial));
			Vec3 relative = target.subtract(camera.pos);
			Vector4f clip = worldToClip.transform(new Vector4f((float) relative.x, (float) relative.y, (float) relative.z, 1f));
			MarkerMath.Placement placement = MarkerMath.place(clip.x, clip.y, clip.w, graphics.guiWidth(), graphics.guiHeight(), EDGE_MARGIN);
			if (placement == null) {
				continue;
			}
			double distance = relative.length();
			String label = config.showDistance ? MarkerMath.formatDistance(distance) : null;
			int color = MarkerMath.withOpacity(ping.color(), config.markerOpacity);
			if (placement.onScreen()) {
				float scale = (float) (MarkerMath.markerScale(distance) * config.markerScale);
				drawDiamond(graphics, minecraft, placement, ping.category(), color, label, textColor, scale);
			} else if (config.showOffscreenArrows) {
				drawArrow(graphics, minecraft, placement, color, label, textColor);
			}
		}
	}

	// Same matrix vanilla's renderLevel builds, so markers sway with view bobbing and hurt shake.
	private static Matrix4f worldToClip(GameRenderer gameRenderer, GameRenderState state, CameraRenderState camera) {
		PoseStack bob = new PoseStack();
		gameRenderer.bobHurt(camera, bob);
		if (state.optionsRenderState.bobView) {
			gameRenderer.bobView(camera, bob);
		}
		return new Matrix4f(camera.projectionMatrix).mul(bob.last().pose()).mul(camera.viewRotationMatrix);
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

	private static void drawDiamond(GuiGraphicsExtractor graphics, Minecraft minecraft, MarkerMath.Placement placement, PingCategory category, int color, String label, int textColor, float scale) {
		Matrix3x2fStack pose = graphics.pose();
		pose.pushMatrix();
		pose.translate((float) placement.x(), (float) placement.y());

		pose.pushMatrix();
		pose.scale(scale, scale);
		pose.pushMatrix();
		pose.rotate((float) (Math.PI / 4));
		frame(graphics, OUTLINE_HALF, color);
		graphics.fill(-FILL_HALF, -FILL_HALF, FILL_HALF, FILL_HALF, color);
		pose.popMatrix();
		if (category != PingCategory.PLAIN) {
			// Tint is white with the marker's alpha, so the icon fades with the diamond.
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, category.sprite(), -ICON / 2, -ICON / 2, ICON, ICON, (color & 0xFF000000) | 0x00FFFFFF);
		}
		pose.popMatrix();

		if (label != null) {
			int diamondTop = (int) Math.ceil(OUTLINE_HALF * Math.sqrt(2) * scale);
			graphics.centeredText(minecraft.font, label, 0, -diamondTop - minecraft.font.lineHeight - 1, textColor);
		}
		pose.popMatrix();
	}

	private static void frame(GuiGraphicsExtractor graphics, int half, int color) {
		graphics.fill(-half, -half, half, -half + 1, color);
		graphics.fill(-half, half - 1, half, half, color);
		graphics.fill(-half, -half + 1, -half + 1, half - 1, color);
		graphics.fill(half - 1, -half + 1, half, half - 1, color);
	}

	private static void drawArrow(GuiGraphicsExtractor graphics, Minecraft minecraft, MarkerMath.Placement placement, int color, String label, int textColor) {
		Matrix3x2fStack pose = graphics.pose();
		pose.pushMatrix();
		pose.translate((float) placement.x(), (float) placement.y());

		pose.pushMatrix();
		pose.rotate((float) placement.arrowAngle());
		graphics.fill(-6, -1, 2, 1, color);
		graphics.fill(0, -4, 2, 4, color);
		graphics.fill(2, -3, 4, 3, color);
		graphics.fill(4, -2, 6, 2, color);
		graphics.fill(6, -1, 7, 1, color);
		pose.popMatrix();

		if (label != null) {
			pose.translate(-(float) Math.cos(placement.arrowAngle()) * 20, -(float) Math.sin(placement.arrowAngle()) * 14);
			graphics.centeredText(minecraft.font, label, 0, -minecraft.font.lineHeight / 2, textColor);
		}
		pose.popMatrix();
	}
}
