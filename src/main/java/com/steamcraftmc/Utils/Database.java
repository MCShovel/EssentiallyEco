package com.steamcraftmc.Utils;

import com.steamcraftmc.EcoPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {
	private final EcoPlugin plugin;

	private Connection connection;
	private String tbl_accounts, tbl_history, tbl_version;

	public Database(EcoPlugin plugin) {
		this.plugin = plugin;
		tbl_accounts = "eco_accounts";
		tbl_history = "eco_history";
		tbl_version = "eco_version";
	}

	public boolean init() {
		try {
			if (!checkConnection())
				return false;
			
			ResultSet set = connection.prepareStatement("SHOW TABLES LIKE '" + tbl_accounts + "'").executeQuery();
			boolean newDatabase = set.next();
	
			set.close();
	
			query("CREATE TABLE IF NOT EXISTS `" + tbl_accounts + "` (" + "  `uuid` varchar(40) NOT NULL,"
					+ "  `name` varchar(64) NOT NULL," + "  `money` double(10,2) NOT NULL DEFAULT '0.00',"
					+ "  PRIMARY KEY (`uuid`)," + "  INDEX `fe_account_byname` (`name`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
	
			query("CREATE TABLE IF NOT EXISTS `" + tbl_history + "` (" + "  `id` int(11) NOT NULL AUTO_INCREMENT,"
					+ "  `uuid` varchar(36) NOT NULL," + "  `payee` varchar(36) DEFAULT NULL,"
					+ "  `amtChange` double NOT NULL," + "  `locX` int(11) DEFAULT NULL,"
					+ "  `locY` int(11) DEFAULT NULL," + "  `locZ` int(11) DEFAULT NULL,"
					+ "  `locYaw` float(4,3) DEFAULT NULL," + "  `locPitch` float(4,3) DEFAULT NULL,"
					+ "  `unixTime` bigint(20) DEFAULT NULL," + "  PRIMARY KEY (`id`),"
					+ "  INDEX fe_history_byuser (uuid, unixTime)" + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;");
	
			query("CREATE TABLE IF NOT EXISTS `" + tbl_version + "` (" + "  `version` int NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
	
			if (newDatabase) {
				int version = getVersion();
	
				if (version == 0) {
					setVersion(1);
				}
			} else {
				setVersion(1);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean checkConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = getNewConnection();

				if (connection == null || connection.isClosed()) {
					return false;
				}

				return connection.createStatement().execute("SELECT 1;");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection = getNewConnection();
		}

		return false;
	}

	protected Connection getNewConnection() {
		close();

		String prefix = plugin.Config.get("mysql.tableprefix", "eco_");
		tbl_accounts = prefix + "_accounts";
		tbl_history = prefix + "history";
		tbl_version = prefix + "version";

		String host = plugin.Config.get("mysql.host", "localhost");
		int port = plugin.Config.getInt("mysql.port", 3306);
		String user = plugin.Config.get("mysql.user", "root");
		String password = plugin.Config.get("mysql.password", "password");
		String database = plugin.Config.get("mysql.database", "economy");
				
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
			return DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			return null;
		}
	}

	protected boolean query(String sql) throws SQLException {
		return connection.createStatement().execute(sql);
	}

	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected int getVersion() {
		checkConnection();

		int version = 0;

		try {
			ResultSet set = connection.prepareStatement("SELECT version from " + tbl_version).executeQuery();

			if (set.next()) {
				version = set.getInt("version");
			}

			set.close();

			return version;
		} catch (Exception e) {
			e.printStackTrace();

			return version;
		}
	}

	protected void setVersion(int version) {
		checkConnection();

		try {
			connection.prepareStatement("DELETE FROM " + tbl_version).executeUpdate();

			connection.prepareStatement("INSERT INTO " + tbl_version + " (version) VALUES (" + version + ")")
					.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Account> getTopAccounts(int size) {
		checkConnection();

		String sql = "SELECT name, uuid, money FROM " + tbl_accounts + " ORDER BY money DESC limit " + size;

		List<Account> topAccounts = new ArrayList<Account>();

		try {
			ResultSet set = connection.createStatement().executeQuery(sql);

			while (set.next()) {
				Account account = new Account(set.getString("name"), UUID.fromString(set.getString("uuid")),
						set.getDouble("money"));
				topAccounts.add(account);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return topAccounts;
	}

	public void createMissingAccount(String name, UUID uniqueId, double initialBalance) {
		checkConnection();

		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tbl_accounts
					+ " (name, uuid, money) VALUES (?, ?, ?)" + "ON DUPLICATE KEY UPDATE name = ?;");

			statement.setString(1, name);
			statement.setString(2, uniqueId.toString());
			statement.setDouble(3, initialBalance);
			statement.setString(4, name);

			statement.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Account getAccountByName(String name) {
		checkConnection();
		Account result = null;

		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT name, uuid, money FROM " + tbl_accounts + " WHERE name = ?;");
			statement.setString(1, name);

			ResultSet set = statement.executeQuery();
			if (set.next()) {
				result = new Account(set.getString("name"), UUID.fromString(set.getString("uuid")),
						set.getDouble("money"));
			} else {
				set.close();

				statement = connection.prepareStatement(
						"SELECT name, uuid, money FROM " + tbl_accounts + " WHERE name like ? + '%';");
				statement.setString(1, name);

				set = statement.executeQuery();
				if (set.next()) {
					result = new Account(set.getString("name"), UUID.fromString(set.getString("uuid")),
							set.getDouble("money"));
				}
			}

			if (set.next()) {
				result = null; // found multiple?
			}

			set.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public Account getAccount(UUID uniqueId) {
		checkConnection();
		Account result = null;

		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT name, uuid, money FROM " + tbl_accounts + " WHERE uuid = ?;");
			statement.setString(1, uniqueId.toString());

			ResultSet set = statement.executeQuery();
			if (set.next()) {
				result = new Account(set.getString("name"), UUID.fromString(set.getString("uuid")),
						set.getDouble("money"));
			}

			set.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public void transferFunds(Account account, Account receiver, double money) throws SQLException {
		addRemoveFunds(account, -money);
		try {
			addRemoveFunds(receiver, money);
		}
		catch(SQLException e) {
			addRemoveFunds(account, money);
			throw e;
		}
	}

	public void addRemoveFunds(Account account, double money) throws SQLException {
		checkConnection();

		try {
			PreparedStatement statement = connection.prepareStatement(
					"UPDATE " + tbl_accounts + " SET money = (money + ?) WHERE uuid = ? AND (money + ?) >= 0.0;");

			statement.setDouble(1, money);
			statement.setString(2, account.Uuid.toString());
			statement.setDouble(3, money);

			if (1 != statement.executeUpdate()) {
				throw new SQLException("Insufficient funds");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void setBalance(Account account, double money) throws SQLException {
		checkConnection();

		try {
			PreparedStatement statement = connection
					.prepareStatement("UPDATE " + tbl_accounts + " SET money = ? WHERE uuid = ?;");

			statement.setDouble(1, money);
			statement.setString(2, account.Uuid.toString());
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}