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

	public double getInitialBalance() {
		return getDouble("balance.initial", 0.0);
	}

	public String getPrefix() {
		return get("currency.prefix", "&7$&f");
	}

	public String getSuffix() {
		return get("currency.suffix", "");
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
		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		formatSymbols.setDecimalSeparator('.');

		String pattern = "###,###,##0";

		int digits = getMinorDigits();
		boolean isWholeNumber = value == Math.round(value);

		if (digits > 0 && !isWholeNumber) {
			pattern += '.';
			for (int ix = 0; ix < digits; ix++) {
				pattern += '0';
			}
		}

		DecimalFormat df = new DecimalFormat(pattern, formatSymbols);

		return df.format(value);
	}

	public String format(double amount) {
		long digitpow = (long)Math.pow(10.0, (double)getMinorDigits());
		long lamount = (long) (amount * digitpow);

		String suffix = getSuffix() + "";

		if (hasMinorName() && lamount > 0 && lamount < digitpow) {
			if (lamount == 1) {
				suffix += ' ' + getMinorSigular();
			} else {
				suffix += ' ' + getMinorPlural();
			}
			amount = (double)lamount;
		} else if (lamount == digitpow) {
			suffix += ' ' + getMajorSigular();
		} else {
			suffix += ' ' + getMajorPlural();
		}

		return getPrefix() + formatValue(amount) + suffix;
	}

	public double floor(double amount) {
		long digitpow = (long)Math.pow(10.0, (double)getMinorDigits());
		long lamount = (long) (amount * digitpow);
		return (double)lamount / digitpow;
	}

}
