package com.steamcraftmc.Commands;

import com.steamcraftmc.EcoPlugin;
import com.steamcraftmc.Utils.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSet extends BaseCommand {
	public CmdSet(EcoPlugin plugin) {
		super(plugin, "set", "essentials.eco", 2);
	}

	@Override
	protected boolean doCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length < 2) {
			return false;
		}

		double money;

		try {
			money = Double.parseDouble(args[1]);
		} catch (NumberFormatException e) {
			return false;
		}

		Account account = plugin.Database.getAccountByName(args[0]);

		if (account == null) {
			sender.sendMessage(plugin.Message.UserNotFound(args[0]));
			return true;
		}

		try {
			plugin.Database.setBalance(account, money);
		} catch (Exception e) {
			sender.sendMessage(plugin.Message.get("pay.failed", "&4Unable to transfer funds."));
			return true;
		}

		String txtAmount = plugin.Config.format(money);
		sender.sendMessage(plugin.Message.format("pay.sent", "&6You have sent {amount}&6 to &f{name}&6.", "name",
				account.Name, "amount", txtAmount));

		Player receiverPlayer = plugin.getServer().getPlayer(account.Uuid);
		if (receiverPlayer != null) {
			receiverPlayer.sendMessage(plugin.Message.format("pay.sent", "&6You have received {amount}&6 from &f{name}&6.",
					"name", sender.getName(), "amount", txtAmount));
		}
		return true;
	}
}
