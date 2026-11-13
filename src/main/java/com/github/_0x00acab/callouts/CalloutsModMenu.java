package com.github._0x00acab.callouts;

//? if fabric {
import com.github._0x00acab.callouts.client.CalloutsConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

// Only loaded by ModMenu, so the mod still runs without it installed.
public class CalloutsModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return CalloutsConfigScreen::new;
	}
}
//?}
