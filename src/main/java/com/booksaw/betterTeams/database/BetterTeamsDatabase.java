package com.booksaw.betterTeams.database;

import com.booksaw.betterTeams.database.api.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BetterTeamsDatabase extends Database {

	public void setupTables() {
		createTableIfNotExists(TableName.TEAM,
				"teamID VARCHAR(50) NOT NULL PRIMARY KEY, name VARCHAR(50) NOT NULL, description VARCHAR(300), open BOOLEAN DEFAULT 0, score INT DEFAULT 0, money DOUBLE DEFAULT 0, home VARCHAR(200), color CHAR(1) DEFAULT '6', echest TEXT(20000), level INT DEFAULT 1, tag VARCHAR(50), pvp BOOLEAN DEFAULT 0");

		createTableIfNotExists(TableName.PLAYERS,
				"playerUUID VARCHAR(50) NOT NULL PRIMARY KEY, teamID VARCHAR(50) NOT NULL, playerRank INT NOT NULL, title VARCHAR(100), FOREIGN KEY (teamID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.ALLYREQUESTS,
				"requestingTeamID VARCHAR(50) NOT NULL, receivingTeamID VARCHAR(50) NOT NULL, PRIMARY KEY(requestingTeamID, receivingTeamID), FOREIGN KEY (requestingTeamID) REFERENCES "
						+ TableName.TEAM
						+ "(teamID) ON DELETE CASCADE, FOREIGN KEY (receivingTeamID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.WARPS,
				"TeamID VARCHAR(50) NOT NULL, warpInfo VARCHAR(200) NOT NULL, PRIMARY KEY(TeamID, warpInfo), FOREIGN KEY (TeamID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.CHESTCLAIMS,
				"TeamID VARCHAR(50) NOT NULL, chestLoc VARCHAR(50) NOT NULL, PRIMARY KEY(TeamID, chestLoc), FOREIGN KEY (TeamID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.BANS,
				"PlayerUUID VARCHAR(50) NOT NULL, TeamID VARCHAR(50) NOT NULL, PRIMARY KEY(PlayerUUID, TeamID), FOREIGN KEY (teamID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");

		createTableIfNotExists(TableName.ALLIES,
				"team1ID VARCHAR(50) NOT NULL, team2ID VARCHAR(50) NOT NULL, PRIMARY KEY(team1ID, team2ID), FOREIGN KEY (team1ID) REFERENCES "
						+ TableName.TEAM
						+ "(teamID) ON DELETE CASCADE, FOREIGN KEY (team2ID) REFERENCES "
						+ TableName.TEAM + "(teamID) ON DELETE CASCADE");
	}

	public PreparedStatement select(String select, TableName from) {
		return executeQuery("SELECT %s FROM %s".formatted(select, from.toString()));
	}

	public PreparedStatement selectWhere(String select, TableName from, String where) {
		return executeQuery("SELECT %s FROM %s WHERE %s;".formatted(select, from.toString(), where));
	}

	public PreparedStatement selectOrder(String select, TableName from, String orderBy) {
		return executeQuery("SELECT %s FROM %s ORDER BY %s;".formatted(select, from.toString(), orderBy));
	}

	public PreparedStatement selectInnerJoinGroupByOrder(String select, TableName table, TableName joinTable,
														 String columToJoin, String groupBy, String orderBy) {
		return executeQuery("SELECT %s FROM %s INNER JOIN %s on (%s) GROUP BY %s ORDER BY %s;".formatted(select,
				table.toString(), joinTable.toString(), columToJoin, groupBy, orderBy));
	}

	public boolean hasResult(TableName from, String where) {
		try (PreparedStatement ps = selectWhere("*", from, where)) {
			ResultSet result = ps.executeQuery();
			if (result == null) {
				return false;
			}
			return result.first();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getResult(String column, TableName from, String where) {
		try (PreparedStatement pr = selectWhere(column, from, where)) {
			ResultSet results = pr.executeQuery();
			results.first();
			return results.getString(column);
		} catch (SQLException e) {
			return "";
		}
	}

	public void deleteRecord(TableName table, String condition) {
		executeStatement("DELETE FROM %s WHERE %s;".formatted(table.toString(), condition));
	}

	/**
	 * imagine 
	 *
	 * @param table     the table the record is in
	 * @param field     the column to update
	 * @param update    the value to set (bound safely via PreparedStatement)
	 * @param condition the goon condition
	 */
	public void updateRecordWhere(TableName table, String field, Object update, String condition) {
		executeStatement("UPDATE %s SET %s = ? WHERE %s;".formatted(table.toString(), field, condition), update);
	}

	public void updateRecord(TableName table, String update) {
		executeStatement("UPDATE %s SET %s".formatted(table.toString(), update));
	}

	public void insertRecord(TableName table, String columns, String values) {
		executeStatement("INSERT INTO %s (%s) VALUES (%s);".formatted(table.toString(), columns, values));
	}

	public void insertRecordIfNotExists(TableName table, String columns, String values) {
		executeStatement("INSERT IGNORE INTO %s (%s) VALUES (%s);".formatted(table.toString(), columns, values));
	}
}
