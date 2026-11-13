package com.github._0x00acab.callouts.client;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

// Plain vanilla widgets so both loaders can show it without a config library.
public final class CalloutsConfigScreen extends OptionsSubScreen {
	public CalloutsConfigScreen(Screen parent) {
		super(parent, Minecraft.getInstance().options, Component.translatable("callouts.config.title"));
	}

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
			button -> minecraft.gui.setScreen(new SoundPickerScreen(this))).build();
		list.addSmall(soundButton,
			slider("callouts.config.sound_volume", 0.0, 1.0, config.soundVolume, value -> config.soundVolume = value).createButton(options));

		list.addHeader(Component.translatable("callouts.config.controls"));
		list.addSmall(
			new OptionInstance<>("callouts.config.wheel_mode", OptionInstance.noTooltip(),
				(caption, value) -> Options.genericValueLabel(caption, Component.translatable(value ? "callouts.config.wheel_mode.toggle" : "callouts.config.wheel_mode.hold")),
				new OptionInstance.Enum<>(List.of(false, true), Codec.BOOL),
				config.wheelToggle,
				value -> config.wheelToggle = value));
	}

	@Override
	public void removed() {
		super.removed();
		PingClient.config().save();
	}

	// Percent-labelled slider over [min, max], built on vanilla's 0..1 slider.
	private static OptionInstance<Double> slider(String key, double min, double max, double initial, OptionInstance.ValueUpdateListener<Double> onChange) {
		return new OptionInstance<>(key, OptionInstance.noTooltip(),
			(caption, value) -> Options.genericValueLabel(caption, Component.literal(Math.round(value * 100) + "%")),
			OptionInstance.UnitDouble.INSTANCE.xmap(slider -> min + slider * (max - min), value -> (value - min) / (max - min)),
			initial, onChange);
	}
}
