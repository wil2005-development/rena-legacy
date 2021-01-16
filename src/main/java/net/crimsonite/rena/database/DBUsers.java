package net.crimsonite.rena.database;

import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.gen.ast.Table;

public class DBUsers {
	
	private static final Db db = DBConnection.db();
	
	public static Table users() {
		return db.table("users");
	}

}
