package com.steamcraftmc.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.steamcraftmc.EcoPlugin;

public class PlayerEvents implements Listener {
	private final EcoPlugin plugin;

	public PlayerEvents(EcoPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		plugin.Database.createMissingAccount(player.getName(), player.getUniqueId(), plugin.Config.getInitialBalance());
	}
}
