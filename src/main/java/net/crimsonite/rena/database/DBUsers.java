package net.crimsonite.rena.database;

import java.util.HashMap;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Table;

public class DBUsers {
	
	public static Table user = DBConnection.users();
	public static final RethinkDB r = RethinkDB.r;
	
	// These are just placeholders. Will fix them later.
	public static void registerUser(String UID) {
		user.insert(r.array(r.hashMap("id", UID).with("title", "chicken"))).runNoReply(DBConnection.conn());
	}
	
	public static String title(String UID) {
		HashMap<String, String> title = user.get(UID).run(DBConnection.conn());
		return title.get("title").toString();
	}

}
