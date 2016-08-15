package com.steamcraftmc.Commands;

import com.steamcraftmc.EcoPlugin;
import com.steamcraftmc.Utils.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdPay extends BaseCommand {
	public CmdPay(EcoPlugin plugin) {
		super(plugin, "pay", "essentials.pay", 2);
	}

	@Override
	protected boolean doCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (args.length < 2) {
			return false;
		}
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;
		double money;

		try {
			money = Double.parseDouble(args[1]);
			money = plugin.Config.floor(money);
		} catch (NumberFormatException e) {
			return false;
		}

		if (money <= 0.0) {
			return false;
		}

		Account receiver = plugin.Database.getAccountByName(args[0]);

		if (receiver == null) {
			sender.sendMessage(plugin.Message.UserNotFound(args[0]));
			return true;
		}

		Account account = plugin.Database.getAccount(player.getUniqueId());

		if (account.Uuid.equals(receiver.Uuid)) {
			sender.sendMessage(plugin.Message.get("pay.not-yourself", "&4You can not pay yourself."));
			return true;
		}

		if (account.Amount < money) {
			sender.sendMessage(plugin.Message.get("pay.insufficient-funds", "&4You do not have that much."));
			return true;
		}

		try {
			plugin.Database.transferFunds(account, receiver, money);
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(plugin.Message.getFailed());
			return true;
		}

		String txtAmount = plugin.Config.format(money);
		sender.sendMessage(plugin.Message.format("pay.sent", "&6You have sent {amount}&6 to &f{name}&6.", "name",
				receiver.Name, "amount", txtAmount));

		Player receiverPlayer = plugin.getServer().getPlayer(receiver.Uuid);
		if (receiverPlayer != null) {
			receiverPlayer.sendMessage(plugin.Message.format("pay.received", "&6You have received {amount}&6 from &f{name}&6.",
					"name", account.Name, "amount", txtAmount));
		}

		return true;
	}
}
