package net.crimsonite.rena.database;

import java.util.HashMap;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Connection;

public class DBUsers {
	
	private static Table user = DBConnection.users();
	private static final RethinkDB r = RethinkDB.r;
	private static final Connection conn = DBConnection.conn();
	
	/**
	 * (Write)Registers the user to the database when called.
	 * 
	 * @param UID -The Unique ID of the user.
	 */
	public static void registerUser(String UID) {
		// Roleplay stats are uppercased on purpose.
		user.insert(r.array(
				r.hashMap("id", UID)
				.with("LEVEL", 0)
				.with("EXP", 0)
				.with("ATK", 6)
				.with("DEF", 3)
				.with("HP", 10)
				.with("MP", 0)
				.with("MONEY", 0)
				.with("REP", 0)
				.with("DAILYSTREAK", 0)
				)).runNoReply(conn);
	}
	
	/**
	 * (Write)Increments an integer value from db.
	 * 
	 * @param UID -The Unique ID of the user.
	 * @param key -The db variable to be incremented.
	 * @param val -The amount to increment.
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static void incrementValue(String UID, String key, int val) throws NullPointerException {
		HashMap<String, String> obj = user.get(UID).run(conn);
		int initialValue = Integer.parseInt(String.valueOf(obj.get(key)));
		int incrementedValue = initialValue += val;
		
		user.get(UID).update(r.hashMap(key, incrementedValue)).runNoReply(conn);
	}
	
	/**
	 * (Write)Decrements an integer value from db.
	 * 
	 * @param UID -The Unique ID of the user.
	 * @param key -The db variable to be decremented.
	 * @param val -The amount to decrement.
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static void decrementValue(String UID, String key, int val) throws NullPointerException {
		HashMap<String, String> obj = user.get(UID).run(conn);
		int initialValue = Integer.parseInt(String.valueOf(obj.get(key)));
		int decrementedValue = initialValue -= val;
		
		user.get(UID).update(r.hashMap(key, decrementedValue)).runNoReply(conn);
	}
	
	/**
	 * (Write)Modifies a String value from db.
	 * 
	 * @param UID -The Unique ID of the user.
	 * @param key -the db variable to be modified.
	 * @param val -The value to exchange.
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static void modifyDataString(String UID, String key, String val) throws NullPointerException {
		user.get(UID).update(r.hashMap(key, val)).runNoReply(conn);
	}
	
	/**
	 * (Write)Modifies an Int value from db.
	 * 
	 * @param UID -The Unique ID of the user.
	 * @param key -The db variable to be modified.
	 * @param val =The value to exchange.
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static void modifyDataInt(String UID, String key, int val) throws NullPointerException {
		HashMap<String, String> obj = user.get(UID).run(conn);
		int initialValue = Integer.parseInt(String.valueOf(obj.get(key)));
		
		user.get(UID).update(r.hashMap(key, initialValue+=val)).runNoReply(conn);
	}
	
	/**
	 * (Write)Modifies a Boolean value from db.
	 * 
	 * @param UID -The Unique ID of the user.
	 * @param key -The db variable to be modified.
	 * @param val -The value to exchange.
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static void modifyDataBoolean(String UID, String key, boolean val) throws NullPointerException {
		user.get(UID).update(r.hashMap(key, val)).runNoReply(conn);
	}
	
	/**
	 * (Read)Returns the String value of the specified key.
	 * 
	 * @param UID -The Unique ID of the user.
	 * @param key -The db variable to get.
	 * @return value of key
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static String getValueString(String UID, String key) throws NullPointerException {
		HashMap<String, String> obj = user.get(UID).run(conn);
		
		return String.valueOf(obj.get(key));
	}
	
	/**
	 * (Read)Returns the Integer value of the specified key.
	 * 
	 * @param UID -The Unique ID of the user.
	 * @param key -The db variable to get.
	 * @return value of key
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static int getValueInt(String UID, String key) throws NullPointerException {
		HashMap<String, String> obj = user.get(UID).run(conn);
		
		return Integer.parseInt(obj.get(key));
	}
	
	/**
	 * (Read)Returns the Boolean value of the specified key.
	 * 
	 * @param UID -The Unique ID of the user.
	 * @param key -The db variable to get.
	 * @return value of key
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static Boolean getValueBoolean(String UID, String key) throws NullPointerException {
		HashMap<String, String> obj = user.get(UID).run(conn);
		
		return Boolean.parseBoolean(obj.get(key));
	}

}
