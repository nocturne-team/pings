package com.github._0x00acab.callouts.client;

import com.github._0x00acab.callouts.PingCategory;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
//? if >=1.21 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//? if >=26.4-alpha {
/*import net.minecraft.client.gui.GuiPipeline;
*///?} else
import net.minecraft.client.renderer.RenderPipelines;
//?} else {
/*import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
*///?}

//? if >=1.21 {
// Subclassed only because KeyboardHandler passes keys through for this type; all parent behavior is overridden.
public final class PingWheelScreen extends GameModeSwitcherScreen {
//?} else {
/*// 1.20.1 never passes keys through while a screen is open, so unused keys are forwarded by hand below.
public final class PingWheelScreen extends Screen {
*///?}
	private static final int RADIUS = 60;
	private static final int DEAD_ZONE = 15;
	private static final int TILE_HALF = 16;
	private static final int ICON = 26;

	private final KeyMapping wheelKey;
	// Toggle: a second press of the wheel key picks. Hold: releasing it picks.
	private final boolean toggle;
	private double lastMouseX;
	private double lastMouseY;

	public PingWheelScreen(KeyMapping wheelKey, boolean toggle) {
		//? if <1.21 {
		/*super(Component.empty());
		*///?}
		this.wheelKey = wheelKey;
		this.toggle = toggle;
	}

	@Override
	protected void init() {
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	//? if >=26.4-alpha {
	/*@Override
	public void extractBackground(GuiGraphicsExtractor graphics) {
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics) {
		int mouseX = graphics.mouseX();
		int mouseY = graphics.mouseY();
	*///?} else if >=1.21 {
	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
	//?} else {
	/*@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
	*///?}
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
			//? if >=1.21 {
			//? if >=26.4-alpha {
			/*graphics.blitSprite(GuiPipeline.TEXTURED, category.sprite(), x - ICON / 2, y - ICON / 2, ICON, ICON);
			*///?} else
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, category.sprite(), x - ICON / 2, y - ICON / 2, ICON, ICON);
			graphics.centeredText(font, Component.translatable(category.translationKey()), x, y + TILE_HALF + 3, selected ? 0xFFFFFF55 : 0xFFFFFFFF);
			//?} else {
			/*graphics.blit(PingHud.texture(category.sprite()), x - ICON / 2, y - ICON / 2, 0, 0, ICON, ICON, ICON, ICON);
			graphics.drawCenteredString(font, Component.translatable(category.translationKey()), x, y + TILE_HALF + 3, selected ? 0xFFFFFF55 : 0xFFFFFFFF);
			*///?}
		}
	}

	//? if >=1.21 {
	// Returning false hands the key to the game, so movement keys keep working.
	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.isEscape()) {
			onClose();
			return true;
		}
		if (toggle && wheelKey.matches(event)) {
			commit(lastMouseX, lastMouseY);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyReleased(KeyEvent event) {
		if (!toggle && wheelKey.matches(event)) {
			commit(lastMouseX, lastMouseY);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (!toggle && wheelKey.matchesMouse(event)) {
			commit(event.x(), event.y());
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (event.button() == 0 || (toggle && wheelKey.matchesMouse(event))) {
			commit(event.x(), event.y());
			return true;
		}
		return false;
	}
	//?} else {
	/*@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == InputConstants.KEY_ESCAPE) {
			onClose();
			return true;
		}
		if (toggle && wheelKey.matches(keyCode, scanCode)) {
			commit(lastMouseX, lastMouseY);
			return true;
		}
		// Keeps movement keys working while the wheel is open.
		KeyMapping.set(InputConstants.getKey(keyCode, scanCode), true);
		return true;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (!toggle && wheelKey.matches(keyCode, scanCode)) {
			commit(lastMouseX, lastMouseY);
			return true;
		}
		KeyMapping.set(InputConstants.getKey(keyCode, scanCode), false);
		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (!toggle && wheelKey.matchesMouse(button)) {
			commit(mouseX, mouseY);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 || (toggle && wheelKey.matchesMouse(button))) {
			commit(mouseX, mouseY);
			return true;
		}
		return false;
	}
	*///?}

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
