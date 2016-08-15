package com.steamcraftmc.Utils;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import com.steamcraftmc.EcoPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class VaultHandler implements Economy {

	private static final String EcoName = "EssentiallyEco";
	private final EcoPlugin plugin;
	private boolean isEnabled;

	public VaultHandler(EcoPlugin plugin) {
		this.plugin = plugin;
		this.isEnabled = false;
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(), plugin);
	}

	public void register() {
		plugin.getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, this, plugin,
				ServicePriority.Highest);
		isEnabled = true;
		plugin.log(Level.INFO, "Vault support enabled.");
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public String getName() {
		return EcoName;
	}

	@Override
	public String format(double amount) {
		amount = plugin.Config.floor(amount);
		return ChatColor.stripColor(plugin.Config.format(amount));
	}

	@Override
	public String currencyNameSingular() {
		return plugin.Config.getMajorSigular();
	}

	@Override
	public String currencyNamePlural() {
		return plugin.Config.getMajorPlural();
	}

	@Override
	public double getBalance(String playerName) {
		Account found = plugin.Database.getAccountByName(playerName);
		if (found != null) {
			return found.Amount;
		}
		return 0.0;
	}

	@Override
	public double getBalance(OfflinePlayer offlinePlayer) {
		Account found = plugin.Database.getAccount(offlinePlayer.getUniqueId());
		if (found != null) {
			return found.Amount;
		}
		return 0.0;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		Account found = plugin.Database.getAccountByName(playerName);
		return withdrawOrDeposit(found, true, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		Account found = plugin.Database.getAccount(offlinePlayer.getUniqueId());
		return withdrawOrDeposit(found, true, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		Account found = plugin.Database.getAccountByName(playerName);
		return withdrawOrDeposit(found, false, amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		Account found = plugin.Database.getAccount(offlinePlayer.getUniqueId());
		return withdrawOrDeposit(found, false, amount);
	}

	private EconomyResponse withdrawOrDeposit(Account account, boolean withdraw, double amount) {

		if (account == null) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player account not found.");
		}

		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot use negative values.");
		}

		if (withdraw && account.Amount < amount) {
			return new EconomyResponse(0, account.Amount, ResponseType.FAILURE, "Insufficient funds.");
		}

		try {
			plugin.Database.addRemoveFunds(account, withdraw ? -amount : amount);
			account = account.add(withdraw ? -amount : amount);
		} catch (Exception e) {
			return new EconomyResponse(0, account.Amount, ResponseType.FAILURE, "Failed to transfer funds.");
		}

		return new EconomyResponse(amount, account.Amount, ResponseType.SUCCESS, "");
	}

	@Override
	public boolean has(String playerName, double amount) {
		Account found = plugin.Database.getAccountByName(playerName);
		return found != null && found.Amount >= amount;
	}

	@Override
	public boolean has(OfflinePlayer offlinePlayer, double amount) {
		Account found = plugin.Database.getAccount(offlinePlayer.getUniqueId());
		return found != null && found.Amount >= amount;
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public EconomyResponse createBank(String name, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported.");
	}

	@Override
	public List<String> getBanks() {
		return new ArrayList<String>();
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public boolean hasAccount(String playerName) {
		return null != plugin.Database.getAccountByName(playerName);
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer) {
		return null != plugin.Database.getAccount(offlinePlayer.getUniqueId());
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		throw new NotImplementedException();
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
		plugin.Database.createMissingAccount(offlinePlayer.getName(), offlinePlayer.getUniqueId(),
				plugin.Config.getInitialBalance());
		return true;
	}

	@Override
	public int fractionalDigits() {
		return plugin.Config.getMinorDigits();
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return hasAccount(playerName);
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer, String worldName) {
		return hasAccount(offlinePlayer);
	}

	@Override
	public double getBalance(String playerName, String worldName) {
		return getBalance(playerName);
	}

	@Override
	public double getBalance(OfflinePlayer offlinePlayer, String worldName) {
		return getBalance(offlinePlayer);
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return has(playerName, amount);
	}

	@Override
	public boolean has(OfflinePlayer offlinePlayer, String worldName, double amount) {
		return has(offlinePlayer, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String worldName, double amount) {
		return withdrawPlayer(offlinePlayer, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return depositPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String worldName, double amount) {
		return depositPlayer(offlinePlayer, amount);
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return createPlayerAccount(playerName);
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String worldName) {
		return createPlayerAccount(offlinePlayer);
	}

	public class EconomyServerListener implements Listener {

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (plugin == null) {
				Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(EcoName);

				if (plugin != null && plugin.isEnabled()) {
					VaultHandler.this.isEnabled = true;
					VaultHandler.this.plugin.log(Level.INFO, "Vault support enabled.");
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (plugin != null) {
				VaultHandler.this.isEnabled = false;
				if (event.getPlugin().getDescription().getName().equals(EcoName)) {
					VaultHandler.this.plugin.log(Level.INFO, "Vault support disabled.");
				}
			}
		}

	}
}
