package com.steamcraftmc.Commands;

import com.steamcraftmc.EcoPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdEco extends BaseCommand {
	private final List<BaseCommand> commands;

	public CmdEco(EcoPlugin plugin) {
		super(plugin, "eco", "essentials.eco", 1);
		
		commands = new ArrayList<BaseCommand>();
		commands.add(new CmdPay(plugin));
		commands.add(new CmdBalance(plugin));
		commands.add(new CmdBalanceTop(plugin));
		commands.add(new CmdGive(plugin));
		commands.add(new CmdTake(plugin));
		commands.add(new CmdSet(plugin));
		commands.add(new CmdReload(plugin));
	}

	private BaseCommand getCommand(String name) {
		for (BaseCommand command : commands) {
			String[] aliases = command.getName().split(",");

			for (String alias : aliases) {
				if (alias.equalsIgnoreCase(name)) {
					return command;
				}
			}
		}

		return null;
	}

	private String[] merge(String[]... arrays) {
		int arraySize = 0;

		for (String[] array : arrays) {
			arraySize += array.length;
		}

		String[] result = new String[arraySize];

		int j = 0;

		for (String[] array : arrays) {
			for (String string : array) {
				result[j++] = string;
			}
		}

		return result;
	}

	private void sendDefaultCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = "balance";

		if (!(sender instanceof Player) && args.length < 1) {
			command = "help";
		}

		onCommand(sender, cmd, commandLabel, merge(new String[] { command }, args));
	}

	@Override
	protected boolean doCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length < 1) {
			sendDefaultCommand(sender, cmd, commandLabel, args);
			return true;
		}

		BaseCommand command = getCommand(args[0]);

		if (command == null) {
			sendDefaultCommand(sender, cmd, commandLabel, args);
			return true;
		}

		String[] realArgs = new String[args.length - 1];

		for (int i = 1; i < args.length; i++) {
			realArgs[i - 1] = args[i];
		}

		if (!command.onCommand(sender, cmd, commandLabel, realArgs)) {
			sender.sendMessage(plugin.Message.get("eco.usage", "Usage: /eco <reload|give|take|reset> <name> [amount]"));
		}

		return true;
	}
}
