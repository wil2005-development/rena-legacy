package net.crimsonite.rena.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Connection;

public class DBConnection {
	
	public static Connection connection;
	
	private static final RethinkDB r = RethinkDB.r;
	final static Logger logger = LoggerFactory.getLogger(DBConnection.class);

	/**
	 * @return database connection
	 */
	public static Connection conn() {
		connection = r.connection().hostname("localhost").port(28015).connect();
		
		return connection;
	}
	
	// Primary DB
	protected static Db db() {
		return r.db("Rena");
	}
	
	/**
	 * @return table of users
	 */
	public static Table users() {
		return db().table("users");
	}
	
	/**
	 * @return table of guilds
	 */
	public static Table guilds() {
		return db().table("guilds");
	}
}
