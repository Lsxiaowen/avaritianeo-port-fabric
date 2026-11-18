package net.byAqua3.avaritia.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.byAqua3.avaritia.gui.AvaritiaConfigScreen;

public final class AvaritiaModMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return AvaritiaConfigScreen::new;
	}
}
