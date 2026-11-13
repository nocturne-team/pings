package com.github._0x00acab.callouts;

import java.util.List;
//? if >=1.21 {
import net.minecraft.resources.Identifier;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}

public enum PingCategory {
	PLAIN("plain"),
	DANGER("danger"),
	LOOK_HERE("look_here"),
	COME_HERE("come_here"),
	NEED_HELP("need_help"),
	MINING("mining");

	public static final List<PingCategory> WHEEL = List.of(DANGER, LOOK_HERE, COME_HERE, NEED_HELP, MINING);
	private static final PingCategory[] BY_ID = values();

	private final String key;

	PingCategory(String key) {
		this.key = key;
	}

	public static PingCategory byId(int id) {
		return id >= 0 && id < BY_ID.length ? BY_ID[id] : null;
	}

	public String translationKey() {
		return "callouts.category." + key;
	}

	// 26px outlined icon for the wheel.
	//? if >=1.21 {
	public Identifier sprite() {
	//?} else {
	/*public ResourceLocation sprite() {
	*///?}
		return CalloutsMod.id("category/" + key);
	}

	// 13px plain glyph for the HUD marker, drawn 1:1 so it stays crisp.
	//? if >=1.21 {
	public Identifier markerSprite() {
	//?} else {
	/*public ResourceLocation markerSprite() {
	*///?}
		return CalloutsMod.id("marker/" + key);
	}
}
