package com.steamcraftmc.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class Account {
	public final String Name;
	public final UUID Uuid;
	public final double Amount;
	public final double Original;
	
	public final long PreviousTime;
	public final double PreviousBal;

	public Account(ResultSet data) throws SQLException {
		this.Name = data.getString("name");
		this.Uuid = UUID.fromString(data.getString("uuid"));
		this.Amount = this.Original = data.getDouble("money");
		this.PreviousTime = data.getLong("lastseen");
		this.PreviousBal = data.getDouble("lastbal");
	}

	private Account(Account copy, double newBalance) {
		this.Name = copy.Name;
		this.Uuid = copy.Uuid;
		this.Amount = newBalance;
		this.Original = copy.Amount;
		this.PreviousTime = copy.PreviousTime;
		this.PreviousBal = copy.PreviousBal;
	}

	public double delta() {
		return this.Amount - this.Original;
	}

	public Account add(double amount) {
		return new Account(this, this.Amount + amount);
	}

	public Account set(double amount) {
		return new Account(this, amount);
	}
	
	@Override
	public String toString() {
		return String.format("User " + Name + " logon bal: " + Amount 
				+ ", last seen: " + new Date(this.PreviousTime).toString() 
				+ ", last bal: " + this.PreviousBal);
	}
}
