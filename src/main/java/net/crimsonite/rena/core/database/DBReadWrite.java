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

package net.crimsonite.rena.core.database;

import java.util.HashMap;
import java.util.Map;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.net.Connection;
import net.crimsonite.rena.RenaConfig;

public class DBReadWrite {

    private static final RethinkDB r = RethinkDB.r;
    private static final Connection conn = RenaConfig.getRethinkDBConnection();
    private static final Db db = r.db(conn.db());

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
     * (Write)Register player to the database.
     *
     * @param uid Unique ID of the user.
     */
    public static void registerUser(String uid) {
        db.table(Table.USERS.stringValue).insert(r.array(
                r.hashMap("id", uid)
                        .with("Language", "en")
                        .with("Country", "US")
                        .with("Status", null)
                        .with("Birthday", null)
        )).runNoReply(conn);

        db.table(Table.PLAYERS.stringValue).insert(r.array(
                r.hashMap("id", uid)
                        .with("LEVEL", 0)
                        .with("EXP", 0)

                        .with("ATK", 6)
                        .with("DEF", 3)
                        .with("HP", 10)
                        .with("MP", 5)

                        .with("VIT", 5)
                        .with("STR", 3)
                        .with("AGI", 1)
                        .with("INT", 1)
                        .with("WIS", 2)
                        .with("LUK", 1)

                        .with("MONEY", 0)
                        .with("REP", 0)
                        .with("DAILY_STREAK", 0)
                        .with("LAST_DAILY_CLAIM", 0)

                        .with("INVENTORY", r.hashMap("ITEM_0X194", 1)
                                .with("SEED_OF_LIFE", 0)
                                .with("SEED_OF_WISDOM", 0)
                                .with("ELIXIR_OF_LIFE", 0)
                                .with("ELIXIR_OF_MANA", 0))

                        .with("ACHIEVEMENTS", r.hashMap("OWNER", false)
                                .with("DEDICATED", false))
        )).runNoReply(conn);
    }

    /**
     * (Write)Register guild to the database.
     *
     * @param uid Unique ID of the guild.
     */
    public static void registerGuild(String uid) {
        db.table(Table.GUILDS.stringValue).insert(r.array(
                r.hashMap("id", uid)
                        .with("Prefix", "~")
        )).runNoReply(conn);
    }

    /**
     * (Write)Increments an integer from db.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param key   DB variable to increment.
     * @param val   Amount to increment by.
     * @throws IllegalArgumentException Provided value is a negative number
     * @throws NullPointerException     User/guild is not found in the database.
     */
    public static void incrementValue(Table table, String uid, String key, int val) throws IllegalArgumentException, NullPointerException {
        if (val <= 0) {
            throw new IllegalArgumentException("Value cannot be a negative number (%d).".formatted(val));
        }

        HashMap<String, Long> obj = db.table(table.stringValue).get(uid).run(conn);
        int incrementedValue = obj.get(key).intValue() + val;

        db.table(table.stringValue).get(uid).update(r.hashMap(key, incrementedValue)).runNoReply(conn);
    }

    /**
     * (Write)Increments an integer from db.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param map   The map to look for.
     * @param key   Db variable to increment.
     * @param val   Amount to increment by.
     * @throws IllegalArgumentException Provided value is a negative number
     * @throws NullPointerException     User/guild is not found in the database.
     */
    public static void incrementValueFromMap(Table table, String uid, String map, String key, int val) throws IllegalArgumentException, NullPointerException {
        if (val <= 0) {
            throw new IllegalArgumentException("Value cannot be a negative number (%d).".formatted(val));
        }

        HashMap<String, Map<String, Long>> obj = db.table(table.stringValue).get(uid).run(conn);
        Map<String, Long> mapValue = obj.get(map);
        int incrementedValue = mapValue.get(key).intValue() + val;

        db.table(table.stringValue).get(uid).update(r.hashMap(map, r.hashMap(key, incrementedValue))).runNoReply(conn);
    }

    /**
     * (Write)Decrements an integer value from db.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param key   DB variable to decrement.
     * @param val   Amount to decrement by.
     * @throws IllegalArgumentException Provided value is a negative number
     * @throws NullPointerException     User/guild is not found in the database.
     */
    public static void decrementValue(Table table, String uid, String key, int val) throws IllegalArgumentException, NullPointerException {
        if (val <= 0) {
            throw new IllegalArgumentException("Value cannot be a negative number (%d).".formatted(val));
        }

        HashMap<String, Long> obj = db.table(table.stringValue).get(uid).run(conn);
        int decrementedValue = obj.get(key).intValue() - val;

        db.table(table.stringValue).get(uid).update(r.hashMap(key, decrementedValue)).runNoReply(conn);
    }

    /**
     * (Write)Decrements an integer value from db.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param map   The map to look for.
     * @param key   DB variable to decrement.
     * @param val   Amount to decrement by.
     * @throws IllegalArgumentException Provided value is a negative number.
     * @throws NullPointerException     User/guild is not found in the database.
     */
    public static void decrementValueFromMap(Table table, String uid, String map, String key, int val) throws IllegalArgumentException, NullPointerException {
        if (val <= 0) {
            throw new IllegalArgumentException("Value cannot be a negative number (%d).".formatted(val));
        }

        HashMap<String, Map<String, Long>> obj = db.table(table.stringValue).get(uid).run(conn);
        Map<String, Long> mapValue = obj.get(map);
        int decrementedValue = mapValue.get(key).intValue() - val;

        db.table(table.stringValue).get(uid).update(r.hashMap(map, r.hashMap(key, decrementedValue))).runNoReply(conn);
    }

    /**
     * (Untested)
     * (Write)Modifies an integer value from db.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param map   The map to look for.
     * @param key   DB variable to modify.
     * @param val   Amount to exchange with.
     * @throws IllegalArgumentException Provided value is a negative number.
     * @throws NullPointerException     User/guild is not found int the database.
     */
    public static void modifyDataFromMap(Table table, String uid, String map, String key, int val) throws IllegalArgumentException, NullPointerException {
        if (val <= 0) {
            throw new IllegalArgumentException("Value cannot be a negative number (%d).".formatted(val));
        }

        db.table(table.stringValue).get(uid).update(r.hashMap(map, r.hashMap(key, val))).runNoReply(conn);
    }

    /**
     * (Write)Modifies a String value from db.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param key   DB variable to modify.
     * @param val   Amount to exchange with.
     * @throws NullPointerException User/guild is not found in the database.
     */
    public static void modifyDataString(Table table, String uid, String key, String val) throws NullPointerException {
        db.table(table.stringValue).get(uid).update(r.hashMap(key, val)).runNoReply(conn);
    }

    /**
     * (Write)Modifies an Int value from db.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param key   DB variable to be modified.
     * @param val   Amount to exchange with.
     * @throws NullPointerException User/guild is not found in the database.
     */
    public static void modifyDataInt(Table table, String uid, String key, int val) throws NullPointerException {
        db.table(table.stringValue).get(uid).update(r.hashMap(key, val)).runNoReply(conn);
    }

    /**
     * (Write)Modifies a Boolean value from db.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param key   DB variable to be modified/toggled.
     * @param val   State.
     * @throws NullPointerException User/guild is not found in the database.
     */
    public static void modifyDataBoolean(Table table, String uid, String key, boolean val) throws NullPointerException {
        db.table(table.stringValue).get(uid).update(r.hashMap(key, val)).runNoReply(conn);
    }

    /**
     * (Read)Returns the String value of the specified key.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param key   String to get.
     * @return Value of key
     * @throws NullPointerException User/guild is not found in the database.
     */
    public static String getValueString(Table table, String uid, String key) throws NullPointerException {
        HashMap<String, String> obj = db.table(table.stringValue).get(uid).run(conn);

        return obj.get(key);
    }

    /**
     * (Read)Returns the Integer value of the specified key.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param key   Integer to get.
     * @return Value of key
     * @throws NullPointerException User/guild is not found in the database.
     */
    public static int getValueInt(Table table, String uid, String key) throws NullPointerException {
        HashMap<String, Long> obj = db.table(table.stringValue).get(uid).run(conn);

        return obj.get(key).intValue();
    }

    /**
     * (Read)Returns the Boolean value of the specified key.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param key   Variable to get.
     * @return Value of key
     * @throws NullPointerException User/guild is not found in the database.
     */
    public static boolean getValueBoolean(Table table, String uid, String key) throws NullPointerException {
        HashMap<String, Boolean> obj = db.table(table.stringValue).get(uid).run(conn);

        return obj.get(key);
    }

    /**
     * (Read)Returns the Map(String, Integer) value of the specified key.
     *
     * @param table Database table.
     * @param uid   Unique ID of the user/guild.
     * @param key   Long to get.
     * @return Value of key
     * @throws NullPointerException User/guild is not found in the database.
     */
    public static Map<String, Long> getValueMapSL(Table table, String uid, String key) throws NullPointerException {
        HashMap<String, Map<String, Long>> obj = db.table(table.stringValue).get(uid).run(conn);

        return obj.get(key);
    }

}
