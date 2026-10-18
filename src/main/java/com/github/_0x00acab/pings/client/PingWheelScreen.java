package com.github._0x00acab.pings.client;

import com.github._0x00acab.pings.PingCategory;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

// Subclassed only because KeyboardHandler passes keys through for this type; all parent behavior is overridden.
public final class PingWheelScreen extends GameModeSwitcherScreen {
    private static final int RADIUS = 60;
    private static final int DEAD_ZONE = 15;
    private static final int TILE_HALF = 16;
    private static final int ICON = 26;

    private final KeyMapping holdKey;
    private double lastMouseX;
    private double lastMouseY;

    public PingWheelScreen(KeyMapping holdKey) {
        this.holdKey = holdKey;
    }

    @Override
    protected void init() {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        PingCategory hovered = hovered(mouseX, mouseY);
        List<PingCategory> categories = PingCategory.WHEEL;
        int centerX = width / 2;
        int centerY = height / 2;
        for (int i = 0; i < categories.size(); i++) {
            PingCategory category = categories.get(i);
            double angle = WheelMath.sectorCenterAngle(i, categories.size());
            int x = centerX + (int) Math.round(Math.sin(angle) * RADIUS);
            int y = centerY - (int) Math.round(Math.cos(angle) * RADIUS);
            boolean selected = category == hovered;
            graphics.fill(x - TILE_HALF, y - TILE_HALF, x + TILE_HALF, y + TILE_HALF, selected ? 0xC0FFFFFF : 0x90000000);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, category.sprite(), x - ICON / 2, y - ICON / 2, ICON, ICON);
            graphics.centeredText(font, Component.translatable(category.translationKey()), x, y + TILE_HALF + 3, selected ? 0xFFFFFF55 : 0xFFFFFFFF);
        }
    }

    // Returning false hands the key to the game, so movement keys keep working.
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            onClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (holdKey.matches(event)) {
            commit(lastMouseX, lastMouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (holdKey.matchesMouse(event)) {
            commit(event.x(), event.y());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            commit(event.x(), event.y());
            return true;
        }
        return false;
    }

    private PingCategory hovered(double mouseX, double mouseY) {
        int index = WheelMath.sectorAt(mouseX - width / 2.0, mouseY - height / 2.0, PingCategory.WHEEL.size(), DEAD_ZONE);
        return index < 0 ? null : PingCategory.WHEEL.get(index);
    }

    private void commit(double mouseX, double mouseY) {
        PingCategory choice = hovered(mouseX, mouseY);
        onClose();
        if (choice != null) {
            PingClient.sendPing(minecraft, choice);
        }
    }
}
