package net.crimsonite.rena.database;

import java.util.HashMap;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Table;

public class DBUsers {
	
	public static Table user = DBConnection.users();
	public static final RethinkDB r = RethinkDB.r;
	
	/**
	 * (Write)Registers the user to the database when called.
	 * 
	 * @param UID
	 */
	public static void registerUser(String UID) {
		user.insert(r.array(
				r.hashMap("id", UID)
				.with("level", 0)
				.with("exp", 0)
				.with("money", 0)
				.with("rep", 0)
				.with("dailyStreak", 0)
				)).runNoReply(DBConnection.conn());
	}
	
	/**
	 * (Write)Modifies a String value from db.
	 * 
	 * @param UID
	 * @param key
	 * @param val
	 */
	public static void modifyDataString(String UID, String key, String val) {
		user.get(UID).update(r.hashMap(key, val)).runNoReply(DBConnection.conn());
	}
	
	/**
	 * (Write)Modifies an Int value from db.
	 * 
	 * @param UID
	 * @param key
	 * @param val
	 */
	public static void modifyDataInt(String UID, String key, int val) {
		HashMap<String, String> obj = user.get(UID).run(DBConnection.conn());
		int initialValue = Integer.parseInt(String.valueOf(obj.get(key)));
		
		user.get(UID).update(r.hashMap(key, initialValue+=val)).runNoReply(DBConnection.conn());
	}
	
	/**
	 * (Write)Modifies a Boolean value from db.
	 * 
	 * @param UID
	 * @param key
	 * @param val
	 */
	public static void modifyDataBoolean(String UID, String key, boolean val) {
		user.get(UID).update(r.hashMap(key, val)).runNoReply(DBConnection.conn());
	}
	
	/**
	 * (Read)Returns the value of the specified key.
	 * 
	 * @param UID
	 * @param key
	 * @return value of key
	 */
	public static String getValue(String UID, String key) {
		HashMap<String, String> obj = user.get(UID).run(DBConnection.conn());
		
		return String.valueOf(obj.get(key));
	}
	
	

}
