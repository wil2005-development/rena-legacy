/*
 * Copyright (C) 2020-2021  Nhalrath
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
		user.get(UID).update(r.hashMap(key, val)).runNoReply(conn);
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
