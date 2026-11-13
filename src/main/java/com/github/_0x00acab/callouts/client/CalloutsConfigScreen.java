package com.github._0x00acab.callouts.client;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//? if <26.2
//import java.util.function.Consumer;
//? if >=1.21 {
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
*///?}

// Plain vanilla widgets so both loaders can show it without a config library.
public final class CalloutsConfigScreen extends OptionsSubScreen {
	//? if <1.21 {
	/*private OptionsList list;
	*///?}

	public CalloutsConfigScreen(Screen parent) {
		super(parent, Minecraft.getInstance().options, Component.translatable("callouts.config.title"));
	}

	//? if >=1.21 {
	@Override
	protected void addOptions() {
		ClientConfig config = PingClient.config();

		list.addHeader(Component.translatable("callouts.config.markers"));
		list.addSmall(
			slider("callouts.config.marker_scale", ClientConfig.MIN_MARKER_SCALE, ClientConfig.MAX_MARKER_SCALE, config.markerScale, value -> config.markerScale = value),
			slider("callouts.config.marker_opacity", ClientConfig.MIN_MARKER_OPACITY, 1.0, config.markerOpacity, value -> config.markerOpacity = value),
			OptionInstance.createBoolean("callouts.config.show_distance", config.showDistance, value -> config.showDistance = value),
			OptionInstance.createBoolean("callouts.config.show_offscreen_arrows", config.showOffscreenArrows, value -> config.showOffscreenArrows = value));

		list.addHeader(Component.translatable("callouts.config.sound_header"));
		// Opens a picker instead of cycling, since players can add their own files.
		Button soundButton = Button.builder(
			Options.genericValueLabel(Component.translatable("callouts.config.sound"), PingSounds.label(config.sound)),
			//? if >=26.2
			button -> minecraft.gui.setScreen(new SoundPickerScreen(this))).build();
			//? if <26.2
			//button -> minecraft.setScreen(new SoundPickerScreen(this))).build();
		list.addSmall(soundButton,
			slider("callouts.config.sound_volume", 0.0, 1.0, config.soundVolume, value -> config.soundVolume = value).createButton(options));
		list.addSmall(errorSound(config));

		list.addHeader(Component.translatable("callouts.config.controls"));
		list.addSmall(wheelMode(config));
	}
	//?} else {
	/*// 1.20.1's options list holds only options, so the sound picker button sits next to Done.
	@Override
	protected void init() {
		ClientConfig config = PingClient.config();
		list = new OptionsList(minecraft, width, height, 32, height - 32, 25);
		list.addSmall(new OptionInstance<?>[] {
			slider("callouts.config.marker_scale", ClientConfig.MIN_MARKER_SCALE, ClientConfig.MAX_MARKER_SCALE, config.markerScale, value -> config.markerScale = value),
			slider("callouts.config.marker_opacity", ClientConfig.MIN_MARKER_OPACITY, 1.0, config.markerOpacity, value -> config.markerOpacity = value),
			OptionInstance.createBoolean("callouts.config.show_distance", config.showDistance, value -> config.showDistance = value),
			OptionInstance.createBoolean("callouts.config.show_offscreen_arrows", config.showOffscreenArrows, value -> config.showOffscreenArrows = value),
			slider("callouts.config.sound_volume", 0.0, 1.0, config.soundVolume, value -> config.soundVolume = value),
			errorSound(config),
			wheelMode(config),
		});
		addWidget(list);
		addRenderableWidget(Button.builder(
			Options.genericValueLabel(Component.translatable("callouts.config.sound"), PingSounds.label(config.sound)),
			button -> minecraft.setScreen(new SoundPickerScreen(this))).bounds(width / 2 - 155, height - 27, 150, 20).build());
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
			.bounds(width / 2 + 5, height - 27, 150, 20).build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		basicListRender(graphics, list, mouseX, mouseY, partialTick);
	}
	*///?}

	@Override
	public void removed() {
		super.removed();
		PingClient.config().save();
	}

	private static OptionInstance<Boolean> errorSound(ClientConfig config) {
		return OptionInstance.createBoolean("callouts.config.error_sound", config.errorSound, value -> {
			config.errorSound = value;
			if (value) {
				PingSounds.error();
			}
		});
	}

	private static OptionInstance<Boolean> wheelMode(ClientConfig config) {
		return new OptionInstance<>("callouts.config.wheel_mode", OptionInstance.noTooltip(),
			(caption, value) -> Options.genericValueLabel(caption, Component.translatable(value ? "callouts.config.wheel_mode.toggle" : "callouts.config.wheel_mode.hold")),
			new OptionInstance.Enum<>(List.of(false, true), Codec.BOOL),
			config.wheelToggle,
			value -> config.wheelToggle = value);
	}

	// Percent-labelled slider over [min, max], built on vanilla's 0..1 slider.
	//? if >=26.2 {
	private static OptionInstance<Double> slider(String key, double min, double max, double initial, OptionInstance.ValueUpdateListener<Double> onChange) {
	//?} else {
	/*private static OptionInstance<Double> slider(String key, double min, double max, double initial, Consumer<Double> onChange) {
	*///?}
		return new OptionInstance<>(key, OptionInstance.noTooltip(),
			(caption, value) -> Options.genericValueLabel(caption, Component.literal(Math.round(value * 100) + "%")),
			OptionInstance.UnitDouble.INSTANCE.xmap(slider -> min + slider * (max - min), value -> (value - min) / (max - min)),
			initial, onChange);
	}
}
