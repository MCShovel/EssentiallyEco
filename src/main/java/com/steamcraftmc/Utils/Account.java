package com.steamcraftmc.Utils;

import java.util.UUID;

public class Account {
	public final String Name;
	public final UUID Uuid;
	public double Amount;
	public double Original;

	public Account(String name, UUID uuid, double amount) {
		this.Name = name;
		this.Uuid = uuid;
		this.Amount = this.Original = amount;
	}

	private Account(String name, UUID uuid, double amount, double original) {
		this.Name = name;
		this.Uuid = uuid;
		this.Amount = amount;
		this.Original = original;
	}

	public double delta() {
		return this.Amount - this.Original;
	}

	public Account add(double amount) {
		return new Account(Name, Uuid, this.Amount + amount, this.Amount);
	}

	public Account subtract(double amount) {
		return new Account(Name, Uuid, this.Amount - amount, this.Amount);
	}

	public Account set(double amount) {
		return new Account(Name, Uuid, amount, this.Amount);
	}
}
