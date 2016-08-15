package com.steamcraftmc.Commands;

import com.steamcraftmc.EcoPlugin;
import com.steamcraftmc.Utils.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdBalance extends BaseCommand {

	public CmdBalance(EcoPlugin plugin) {
		super(plugin, "balance", "essentials.balance", 0);
	}

	@Override
	protected boolean doCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Account account;

		if (args.length > 0 && sender.hasPermission("essentials.balance.other")) {
			account = plugin.Database.getAccountByName(args[0]);

			if (account == null) {
				sender.sendMessage(plugin.Message.UserNotFound(args[0]));
				return true;
			}

			sender.sendMessage(plugin.Message.format("bal.other", "&6The user &f{amount}&6 has &f{name}&6.",
							"name", account.Name, "amount", plugin.Config.format(account.Amount)));
		} else if (sender instanceof Player) {
			Player player = (Player)sender;
			account = plugin.Database.getAccount(player.getUniqueId());
			
			sender.sendMessage(plugin.Message.format("bal.me", "&6You have &f{amount}&6 in your account.",
					"name", account.Name, "amount", plugin.Config.format(account.Amount)));
		} else {
			// This is console without a player name
			sender.sendMessage(plugin.Message.format("bal.no-name", "&4You must supply a player name."));
			return false;
		}

		return true;
	}
}
