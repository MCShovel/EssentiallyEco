package com.steamcraftmc.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import com.steamcraftmc.EcoPlugin;

public abstract class BaseCommand implements CommandExecutor {

	protected final EcoPlugin plugin;
	private final String name;
	private final String permission;
	private final int minArgs;

	public BaseCommand(EcoPlugin plugin, String name, String permission, int minArgs) {
		this.plugin = plugin;

		this.name = name.split(",")[0];
		this.permission = permission;
		this.minArgs = minArgs;

		PluginCommand cmd = plugin.getCommand(getFirstName());
		if (cmd != null) {
			cmd.setExecutor(this);
		}
	}

	public String getFirstName() {
		return name.split(",")[0];
	}

	public String getName() {
		return name;
	}

	protected abstract boolean doCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);

	public final boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (args.length < this.minArgs) {
			return false;
		}

		if (!sender.hasPermission(this.permission)) {
			sender.sendMessage(plugin.Message.PermissionDenied());
			return true;
		}

		return doCommand(sender, cmd, commandLabel, args);
	}
}
