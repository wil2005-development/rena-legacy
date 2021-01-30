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

	// Connect to host DB
	public static Connection conn() {
		connection = r.connection().hostname("localhost").port(28015).connect();
		
		return connection;
	}
	
	// Primary DB
	public static Db db() {
		return r.db("Rena");
	}
	
	// Users table
	public static Table users() {
		return db().table("Users");
	}
	
	// Guilds table
	public static Table guilds() {
		return db().table("Guilds");
	}
}
