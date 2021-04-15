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
import java.util.Map;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.net.Connection;

public class DBReadWrite {
	
	private static Db db = DBConnection.db();
	private static final Connection conn = DBConnection.conn();
	private static final RethinkDB r = RethinkDB.r;
	
	public enum Table {
		USERS("users"),
		PLAYERS("players"),
		GUILDS("guilds");

		public final String stringValue;
		
		Table(String stringValue) {
			this.stringValue = stringValue;
		}
	}
	
	/**
	 * (Write)Registers the user to the database when called.
	 * 
	 * @param UID -The Unique ID of the user.
	 */
	public static void registerUser(String UID) {
		// Roleplay stats are uppercased on purpose.
		db.table(Table.USERS.stringValue).insert(r.array(
				r.hashMap("id", UID)
				.with("Language", "en")
				.with("Country", "US")
				.with("Status", null)
				.with("Birthday", null)
				)).runNoReply(conn);
		
		db.table(Table.PLAYERS.stringValue).insert(r.array(
				r.hashMap("id", UID)
				.with("LEVEL", 1)
				.with("EXP", 0)
				.with("ATK", 6)
				.with("DEF", 3)
				.with("HP", 10)
				.with("MP", 0)
				.with("MONEY", 0)
				.with("REP", 0)
				.with("DAILY_STREAK", 0)
				.with("LAST_DAILY_CLAIM", 0)
				.with("INVENTORY", r.hashMap("ITEM_0X194", 1)
						.with("SEED_OF_LIFE", 5)
						.with("SEED_OF_WISDOM", 6)
						.with("ELIXIR_OF_LIFE", 1)
						.with("ELIXIR_OF_MANA", 0))
				)).runNoReply(conn);
	}
	
	/**
	 * (Write)Registers the guild to the database when called.
	 * 
	 * @param UID -The Unique ID of the guild.
	 */
	public static void registerGuild(String UID) {
		db.table(Table.GUILDS.stringValue).insert(r.array(
				r.hashMap("id", UID)
				.with("Prefix", "~")
				)).runNoReply(conn);
	}
	
	/**
	 * (Write)Increments an integer value from db.
	 * 
	 * @param table -The table to modify.
	 * @param UID -The Unique ID of the user/guild.
	 * @param key -The db variable to be incremented.
	 * @param val -The amount to increment.
	 * @throws IllegalArgumentException If the provided value is a negative number
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static void incrementValue(Table table, String UID, String key, int val) throws IllegalArgumentException, NullPointerException {
		if (val <= 0) {
			throw new IllegalArgumentException("Value cannot be a negative number (%d).".formatted(val));
		}
		
		HashMap<String, Long> obj = db.table(table.stringValue).get(UID).run(conn);
		int initialValue = Integer.parseInt(String.valueOf(obj.get(key)));
		int incrementedValue = initialValue += val;
		
		db.table(table.stringValue).get(UID).update(r.hashMap(key, incrementedValue)).runNoReply(conn);
	}
	
	/**
	 * (Write)Decrements an integer value from db.
	 * 
	 * @param table -The table to modify.
	 * @param UID -The Unique ID of the user/guild.
	 * @param key -The db variable to be decremented.
	 * @param val -The amount to decrement.
	 * @throws IllegalArgumentException If the provided value is a negative number
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static void decrementValue(Table table, String UID, String key, int val) throws IllegalArgumentException, NullPointerException {
		if (val <= 0) {
			throw new IllegalArgumentException("Value cannot be a negative number (%d).".formatted(val));
		}
		
		HashMap<String, Long> obj = db.table(table.stringValue).get(UID).run(conn);
		int initialValue = Integer.parseInt(String.valueOf(obj.get(key)));
		int decrementedValue = initialValue -= val;
		
		db.table(table.stringValue).get(UID).update(r.hashMap(key, decrementedValue)).runNoReply(conn);
	}
	
	/**
	 * (Write)Modifies a String value from db.
	 * 
	 * @param table -The table to modify.
	 * @param UID -The Unique ID of the user/guild.
	 * @param key -the db variable to be modified.
	 * @param val -The value to exchange.
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static void modifyDataString(Table table, String UID, String key, String val) throws NullPointerException {
		db.table(table.stringValue).get(UID).update(r.hashMap(key, val)).runNoReply(conn);
	}
	
	/**
	 * (Write)Modifies an Int value from db.
	 * 
	 * @param table -The table to modify.
	 * @param UID -The Unique ID of the user/guild.
	 * @param key -The db variable to be modified.
	 * @param val =The value to exchange.
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static void modifyDataInt(Table table, String UID, String key, int val) throws NullPointerException {		
		db.table(table.stringValue).get(UID).update(r.hashMap(key, val)).runNoReply(conn);
	}
	
	/**
	 * (Write)Modifies a Boolean value from db.
	 * 
	 * @param table -The table to modify.
	 * @param UID -The Unique ID of the user/guild.
	 * @param key -The db variable to be modified.
	 * @param val -The value to exchange.
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static void modifyDataBoolean(Table table, String UID, String key, boolean val) throws NullPointerException {
		db.table(table.stringValue).get(UID).update(r.hashMap(key, val)).runNoReply(conn);
	}
	
	/**
	 * (Read)Returns the String value of the specified key.
	 * 
	 * @param table -The table to look for.
	 * @param UID -The Unique ID of the user/guild.
	 * @param key -The db variable to get.
	 * @return value of key
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static String getValueString(Table table, String UID, String key) throws NullPointerException {
		HashMap<String, String> obj = db.table(table.stringValue).get(UID).run(conn);
		
		return obj.get(key);
	}
	
	/**
	 * (Read)Returns the Integer value of the specified key.
	 * 
	 * @param table -The table to look for.
	 * @param UID -The Unique ID of the user/guild.
	 * @param key -The db variable to get.
	 * @return value of key
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static int getValueInt(Table table, String UID, String key) throws NullPointerException {
		HashMap<String, Long> obj = db.table(table.stringValue).get(UID).run(conn);
		
		return Integer.parseInt(String.valueOf(obj.get(key)));
	}
	
	/**
	 * (Read)Returns the Boolean value of the specified key.
	 * 
	 * @param table -The table to look for.
	 * @param UID -The Unique ID of the user/guild.
	 * @param key -The db variable to get.
	 * @return value of key
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static boolean getValueBoolean(Table table, String UID, String key) throws NullPointerException {
		HashMap<String, Boolean> obj = db.table(table.stringValue).get(UID).run(conn);
		
		return obj.get(key);
	}
	
	/**
	 * (Read)Returns the Map(String, Integer) value of the specified key.
	 * 
	 * @param table -The table to look for.
	 * @param UID -The Unique ID of the user/guild.
	 * @param key -The db variable to get.
	 * @return value of key
	 * @throws NullPointerException If the user is not found in the database.
	 */
	public static Map<String, Long> getValueMapSL(Table table, String UID, String key) throws NullPointerException {
		HashMap<String, Map<String, Long>> obj = db.table(table.stringValue).get(UID).run(conn);
		
		return obj.get(key);
	}
	
}
