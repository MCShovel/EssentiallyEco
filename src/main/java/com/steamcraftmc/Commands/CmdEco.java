package com.steamcraftmc.Commands;

import com.steamcraftmc.EcoPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.Map;

public class CmdEco extends BaseCommand {
	private final Map<String, BaseCommand> commands;

	public CmdEco(EcoPlugin plugin) {
		super(plugin, "eco", "essentials.eco", 1);

		new CmdPay(plugin);
		new CmdBalance(plugin);
		new CmdBalanceTop(plugin);
		commands = new TreeMap<String, BaseCommand>(String.CASE_INSENSITIVE_ORDER);
		addCommand(new CmdGive(plugin));
		addCommand(new CmdTake(plugin));
		addCommand(new CmdSet(plugin));
		addCommand(new CmdReload(plugin));
	}

	private void addCommand(BaseCommand cmd) {
		String[] aliases = cmd.getName().split(",");
		for (String alias : aliases) {
			commands.put(alias, cmd);
		}
	}

	private BaseCommand getCommand(String name) {
		return commands.get(name);
	}

	private void sendDefaultCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		onCommand(sender, cmd, commandLabel, new String[] { "help" });
	}

	@Override
	protected boolean doCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length < 1) {
			sendDefaultCommand(sender, cmd, commandLabel, args);
			return true;
		}

		BaseCommand command = getCommand(args[0]);

		if (command == null) {
			return false;
		}
		
		String[] realArgs = Arrays.copyOfRange(args, 1, args.length);
		if (!command.onCommand(sender, cmd, commandLabel, realArgs)) {
			return false;
		}

		return true;
	}
	
	
}
