package com.steamcraftmc;

import com.steamcraftmc.Utils.BaseYamlSettingsFile;

public class MainMessage extends BaseYamlSettingsFile {
	public MainMessage(EcoPlugin plugin) {
		super(plugin, "message.yml");
	}

	public String PermissionDenied() {
		return get("no-access", "&4You do not have permission to this command.");
	}

	public String UserNotFound(String string) {
		return format("player-not-found", "&6The user &4{name}&6 was not found.", "name", string);
	}
}
