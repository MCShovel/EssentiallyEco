package com.steamcraftmc.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.steamcraftmc.EcoPlugin;

public class TabAccountCompleter implements TabCompleter {

	private final EcoPlugin plugin;
	public TabAccountCompleter(EcoPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdlbl, String[] args) {

		if (args.length > 0) {
			Account tmp = plugin.Database.getAccountByName(args[args.length-1]);
			if (tmp != null) {
				List<String> lst = new ArrayList<String>();
				lst.add(tmp.Name);
				return lst;
			}
		}
		
		return null;
	}

}
