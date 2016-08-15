package com.steamcraftmc.Commands;

import com.steamcraftmc.EcoPlugin;
import com.steamcraftmc.Utils.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSet extends BaseCommand {
	public CmdSet(EcoPlugin plugin) {
		super(plugin, "reset,set", "essentials.eco", 2);
	}

	@Override
	protected boolean doCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length < 2) {
			return false;
		}

		double money;

		try {
			money = Double.parseDouble(args[1]);
			money = plugin.Config.floor(money);
		} catch (NumberFormatException e) {
			return false;
		}

		money = Math.max(0, money);
		
		Account account = plugin.Database.getAccountByName(args[0]);

		if (account == null) {
			sender.sendMessage(plugin.Message.UserNotFound(args[0]));
			return true;
		}

		try {
			plugin.Database.setBalance(account, money);
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(plugin.Message.getFailed());
			return true;
		}

		String txtAmount = plugin.Config.format(money);
		sender.sendMessage(plugin.Message.format("set.sent", "&6The account &f{name}&6 has been set to {amount}&6.", "name",
				account.Name, "amount", txtAmount));

		Player receiverPlayer = plugin.getServer().getPlayer(account.Uuid);
		if (receiverPlayer != null) {
			receiverPlayer.sendMessage(plugin.Message.format("set.received", "&6Your account balance is now {amount}&6.",
					"name", sender.getName(), "amount", txtAmount));
		}
		return true;
	}
}
