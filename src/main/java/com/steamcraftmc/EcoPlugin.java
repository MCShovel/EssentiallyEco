package com.steamcraftmc;

import com.steamcraftmc.Commands.CmdEco;
import com.steamcraftmc.Events.PlayerEvents;
import com.steamcraftmc.Utils.*;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class EcoPlugin extends JavaPlugin {

	public final MainConfig Config;
	public final MainMessage Message;
	public final Database Database;
	private final PlayerEvents _listener;

	public EcoPlugin() {
		this.Config = new MainConfig(this);
		this.Config.load();
		this.Message = new MainMessage(this);
		this.Message.load();
		this.Database = new Database(this);
		_listener = new PlayerEvents(this);
	}

	public void onEnable() {
		if (!Database.init()) {
			getLogger().log(Level.SEVERE, 
					"\n*********************************************************************\n"
					+ "EssentiallyEco requires a MySQL configuration.\n"
					+ "Plugin will remain disabled until restart.\n"
					+ "Please correct the configuration and restart the server.\n"
					+ "*********************************************************************\n");

			setEnabled(false);
			return;
		}

		new CmdEco(this);
		setupVault();
		_listener.register();
	}

	public void log(Level level, String message) {
		getLogger().log(level, message);
	}

	public void onDisable() {
		_listener.unregister();
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
