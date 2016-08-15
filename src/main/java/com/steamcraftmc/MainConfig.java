package com.steamcraftmc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainConfig extends com.steamcraftmc.Utils.BaseYamlSettingsFile {
	public MainConfig(EcoPlugin plugin) {
		super(plugin, "config.yml");
	}

	public int getTopListSize() {
		return getInt("settings.defaulttop", 10);
	}

	public String getMessagePrefix() {
		return get("settings.prefix", "&0[&6Economy&0] &r");
	}
	
	public double getInitialBalance() {
		return getDouble("balance.initial", 0.0);
	}

	public double getMinBal() {
		return getDouble("balance.min", 0.0);
	}

	public double getMaxBal() {
		return getDouble("balance.max", Integer.MAX_VALUE);
	}

	public String getPrefix() {
		return get("currency.prefix", "&7$&f");
	}

	public String getMajorSigular() {
		return get("currency.major.singular", "dollar");
	}

	public String getMajorPlural() {
		return get("currency.major.plural", "dollars");
	}

	public int getMinorDigits() {
		return getInt("currency.minor.digits", 2);
	}
	
	public boolean hasMinorName() {
		return getBoolean("currency.minor.enabled", false);
	}

	public String getMinorSigular() {
		return get("currency.minor.singular", "cent");
	}

	public String getMinorPlural() {
		return get("currency.minor.plural", "cents");
	}

	private String formatValue(double value) {
		boolean isWholeNumber = value == Math.round(value);

		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);

		formatSymbols.setDecimalSeparator('.');

		String pattern = isWholeNumber ? "###,###.###" : "###,##0.00";

		DecimalFormat df = new DecimalFormat(pattern, formatSymbols);

		return df.format(value);
	}

	public String format(double amount) {
		long lamount = (long) (amount * 100);

		String suffix = "";

		if (hasMinorName() && lamount > 0 && lamount < 100) {
			if (amount == 1) {
				suffix += ' ' + getMinorSigular();
			} else if (amount < 1.0) {
				suffix += ' ' + getMinorPlural();
			}
		} else if (amount == 100) {
			suffix += getMajorSigular();
		} else {
			suffix += getMajorPlural();
		}

		return getPrefix() + formatValue(amount) + suffix;
	}

}
