package com.github._0x00acab.callouts.client;

//? if fabric {
import com.github._0x00acab.callouts.CalloutsMod;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;

// Exordium redraws the whole HUD at its own fps cap, so markers would only move a few times a second.
// While pings are visible, report an uncapped fps so it redraws every frame; otherwise pass the player's settings through.
public final class ExordiumCompat {
	private static final int UNCAPPED_FPS = 100_000;
	private static boolean done = !FabricLoader.getInstance().isModLoaded("exordium");

	private ExordiumCompat() {}

	// Called each client tick until Exordium has built its main buffer.
	public static void tryInstall() {
		if (done) {
			return;
		}
		try {
			Class<?> base = Class.forName("dev.tr7zw.exordium.ExordiumModBase");
			Object instance = base.getField("instance").get(null);
			if (instance == null || !(boolean) base.getMethod("isInitialized").invoke(instance)) {
				return;
			}
			done = true;
			Object buffer = base.getMethod("getMainBuffer").invoke(instance);
			Field settingsField = buffer.getClass().getDeclaredField("settings");
			settingsField.setAccessible(true);
			@SuppressWarnings("unchecked")
			Supplier<Object> original = (Supplier<Object>) settingsField.get(buffer);
			Class<?> settingsClass = Class.forName("dev.tr7zw.exordium.versionless.config.Config$ComponentSettings");
			Constructor<?> create = settingsClass.getConstructor(boolean.class, int.class);
			Method isEnabled = settingsClass.getMethod("isEnabled");
			settingsField.set(buffer, (Supplier<Object>) () -> {
				Object settings = original.get();
				if (PingClient.TRACKER.active().isEmpty()) {
					return settings;
				}
				try {
					return create.newInstance(isEnabled.invoke(settings), UNCAPPED_FPS);
				} catch (ReflectiveOperationException e) {
					return settings;
				}
			});
			CalloutsMod.LOGGER.info("Hooked Exordium, HUD redraws every frame while pings are visible");
		} catch (ReflectiveOperationException | RuntimeException e) {
			done = true;
			CalloutsMod.LOGGER.warn("Couldn't hook Exordium, markers may stutter while it is enabled", e);
		}
	}
}
//?}
