package com.steamcraftmc.Commands;

import com.steamcraftmc.EcoPlugin;
import com.steamcraftmc.Utils.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CmdBalanceTop extends BaseCommand {
	public CmdBalanceTop(EcoPlugin plugin) {
		super(plugin, "balancetop", "essentials.balancetop", 0);
	}

	@Override
	protected boolean doCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		int lstSize = plugin.Config.getTopListSize(); 
		if (args.length > 0) {
			String txt = args[0];
			if (txt.matches("\\d+")) {
				int tmp = Integer.parseInt(txt);
				if (tmp > 0 && tmp < lstSize * 2) {
					lstSize = tmp;
				}
			}
		}
		
		List<Account> topAccounts = plugin.Database.getTopAccounts(lstSize);
		sender.sendMessage(plugin.Message.get("baltop.head", "&6==================== TOP PLAYERS ===================="));

		for (int i = 0; i < topAccounts.size(); i++) {
			sender.sendMessage(
				plugin.Message.format("baltop.list", "&6{name}&7 - &f{amount}",
						"name", topAccounts.get(i).Name, "amount", plugin.Config.format(topAccounts.get(i).Amount))
				);
		}

		sender.sendMessage(plugin.Message.get("baltop.foot", "&6====================================================="));
		return true;
	}
}
