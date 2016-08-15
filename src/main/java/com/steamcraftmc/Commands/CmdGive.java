package com.steamcraftmc.Commands;

import com.steamcraftmc.EcoPlugin;
import com.steamcraftmc.Utils.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdGive extends BaseCommand {
	public CmdGive(EcoPlugin plugin) {
		super(plugin, "give", "essentials.eco", 2);
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

		Account account = plugin.Database.getAccountByName(args[0]);

		if (account == null) {
			sender.sendMessage(plugin.Message.UserNotFound(args[0]));
			return true;
		}

		try {
			plugin.Database.addRemoveFunds(account, money);
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(plugin.Message.getFailed());
			return true;
		}

		String txtAmount = plugin.Config.format(money);
		sender.sendMessage(plugin.Message.format("give.sent", "&6You have sent {amount}&6 to &f{name}&6.", "name",
				account.Name, "amount", txtAmount));

		Player receiverPlayer = plugin.getServer().getPlayer(account.Uuid);
		if (receiverPlayer != null) {
			receiverPlayer.sendMessage(plugin.Message.format("give.received", "&6You have received {amount}&6 from &f{name}&6.",
					"name", sender.getName(), "amount", txtAmount));
		}
		return true;
	}
}
