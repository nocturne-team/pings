package com.github._0x00acab.callouts.client;

import com.github._0x00acab.callouts.CalloutsMod;
import java.util.Locale;
import net.minecraft.resources.Identifier;

public enum PingSound {
	NONE,
	CLICK_SOFT,
	CLICK_DEEP,
	CLICK_RETRO,
	FOCUS,
	SWIPE_CONFIRM,
	CHIME,
	TICK;

	public static final PingSound DEFAULT = CLICK_SOFT;

	public String id() {
		return name().toLowerCase(Locale.ROOT);
	}

	public String translationKey() {
		return "callouts.sound." + id();
	}

	// Event names match sounds.json, so resource packs can replace any of them.
	public Identifier event() {
		return CalloutsMod.id("ping." + id());
	}

	public static PingSound byId(String id) {
		for (PingSound sound : values()) {
			if (sound.id().equals(id)) {
				return sound;
			}
		}
		return null;
	}
}
