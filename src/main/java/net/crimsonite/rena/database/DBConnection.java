package net.crimsonite.rena.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

public class DBConnection {
	
	public static Connection connection;
	
	public static final RethinkDB r = RethinkDB.r;
	final static Logger logger = LoggerFactory.getLogger(DBConnection.class);
	
	public static Connection conn() {
		connection = r.connection().hostname("localhost").port(28015).connect();
		
		return connection;
	}
}
