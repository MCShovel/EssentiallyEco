package com.steamcraftmc.Commands;

import com.steamcraftmc.EcoPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CmdReload extends BaseCommand {
	public CmdReload(EcoPlugin plugin) {
		super(plugin, "reload", "essentials.eco", 2);
	}

	@Override
	protected boolean doCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		plugin.reloadConfig();
		sender.sendMessage(plugin.Message.get("eco.reloaded", "&6Eco plugin configuration reloaded."));

		return true;
	}
}
