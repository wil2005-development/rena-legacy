package net.crimsonite.rena.database;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Connection;

import net.crimsonite.rena.RenaBot;

public class DBConnection {

	private static final RethinkDB r = RethinkDB.r;

	/**
	 * @param hostName
	 * @param port
	 * @return database connection
	 */
	public static final Connection conn() {
		Connection connection = r.connection().hostname(RenaBot.hostName).port(RenaBot.port).connect();
		
		return connection;
	}
	
	// Primary DB
	protected static final Db db() {
		return r.db("Rena");
	}
	
	/**
	 * @return table of users
	 */
	public static final Table users() {
		return db().table("users");
	}
	
	/**
	 * @return table of guilds
	 */
	public static final Table guilds() {
		return db().table("guilds");
	}
}
