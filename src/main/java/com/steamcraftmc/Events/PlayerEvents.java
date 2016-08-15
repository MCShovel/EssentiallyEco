package com.steamcraftmc.Events;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.steamcraftmc.EcoPlugin;
import com.steamcraftmc.Utils.Account;

import net.md_5.bungee.api.ChatColor;

public class PlayerEvents implements Listener {
	private final EcoPlugin plugin;

	public PlayerEvents(EcoPlugin plugin) {
		this.plugin = plugin;
	}

	public void register() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void unregister() {
		try {
			for (Player p : plugin.getServer().getOnlinePlayers())
				plugin.Database.storeAccountState(p.getUniqueId());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		plugin.Database.createMissingAccount(player.getName(), player.getUniqueId(), plugin.Config.getInitialBalance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					if (!player.isOnline()) {
						return;
					}
					Account acct = plugin.Database.getAccount(player.getUniqueId());
					if (acct.PreviousTime > 0) {
						Date date = new Date(acct.PreviousTime);
						SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
						String formattedDate = sdf.format(date);
						String formattedAmt = plugin.Config.format(acct.Amount);
						double diff = plugin.Config.floor(acct.Amount - acct.PreviousBal);
						String formattedDiff = ChatColor.stripColor(plugin.Config.format(Math.abs(diff)));

						String msg = null;
						if (diff > 0) {
							msg = plugin.Message.format("join-message.increase",
									"&6You last visited on &7{since}&6.\nYour account increased by &2{diff}&6.\n&6Your current balance is: {total}&6.",
									"total", formattedAmt, "diff", formattedDiff, "since", formattedDate);
						} else if (diff < 0) {
							msg = plugin.Message.format("join-message.decrease",
									"&6You last visited on &7{since}&6.\nYour account reduced by &4{diff}&6.\n&6Your current balance is: {total}&6.",
									"total", formattedAmt, "diff", formattedDiff, "since", formattedDate);
						} else {
							msg = plugin.Message.format("join-message.unchanged",
									"&6You last visited on &7{since}&6.\n&6Your current balance is: {total}&6.",
									"total", formattedAmt, "diff", formattedDiff, "since", formattedDate);
						}
						if (msg != null && msg.length() > 0) {
							player.sendMessage(msg.replace("\\n", "\n"));
							plugin.Database.storeAccountState(player.getUniqueId());
							plugin.log(Level.INFO, acct.toString());
						}
					}
				} catch (Exception eIgnore) {
					eIgnore.printStackTrace();
				}
			}

		}, 20);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		plugin.Database.storeAccountState(player.getUniqueId());
	}
}
