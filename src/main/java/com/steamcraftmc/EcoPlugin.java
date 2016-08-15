package com.steamcraftmc;

import com.steamcraftmc.Commands.CmdEco;
import com.steamcraftmc.Events.PlayerEvents;
import com.steamcraftmc.Utils.*;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class EcoPlugin extends JavaPlugin {

	public final MainConfig Config;
	public final MainMessage Message;
	public final Database Database;

	public EcoPlugin() {
		this.Config = new MainConfig(this);
		this.Config.load();
		this.Message = new MainMessage(this);
		this.Message.load();
		this.Database = new Database(this);
	}

	public void onEnable() {
		if (!Database.init()) {
			setEnabled(false);
			return;
		}

		new CmdEco(this);

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerEvents(this), this);

		setupVault();
	}

	public void log(Level level, String message) {
		getLogger().log(level, message);
	}

	public void onDisable() {
		Database.close();
	}

	public void reloadConfig() {
		super.reloadConfig();
		Config.load();
		Message.load();
		Database.close();
		Database.init();
	}

	private void setupVault() {
		Plugin vault = getServer().getPluginManager().getPlugin("Vault");

		if (vault == null) {
			return;
		}

		new VaultHandler(this).register();
	}
}
