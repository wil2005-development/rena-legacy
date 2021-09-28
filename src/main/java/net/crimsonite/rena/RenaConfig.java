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

package net.crimsonite.rena;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RenaConfig {

    private static final Logger logger = LoggerFactory.getLogger(RenaConfig.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String GITHUB_URL = "https://github.com/Nhalrath/Rena";
    public static final String VERSION_STRING = "@VERSION@";
    public static final String GIT_REVISION = "@GIT_REVISION@";

    public static final String TOKEN = getValue("TOKEN");

    private static final long ownerId = Long.parseLong(getValue("OWNER_ID"));
    private static final boolean useSharding = Boolean.parseBoolean(getValue("USE_SHARDING"));
    private static final int totalShards = Integer.parseInt(getValue("SHARD_COUNT"));
    private static final String hostName = getValue("HOST_NAME");
    private static final String prefix = getValue("PREFIX");

    private static Connection rethinkDBConnection;

    /**
     * Initializes and connects to the active RethinkDB database.
     *
     * @throws ReqlDriverError failed to connect to Rena's database.
     */
    public static void initializeRethinkDBConnection() throws ReqlDriverError {
        rethinkDBConnection = RethinkDB.r.connection()
                .hostname(getValue("HOST_NAME"))
                .port(28015)
                .db("Rena")
                .connect();
    }

    /**
     * A connection to Rena's database.
     *
     * @return connection to Rena's database.
     */
    public static Connection getRethinkDBConnection() {
        return rethinkDBConnection;
    }

    /**
     * @return the Id of the bot's owner.
     */
    public static long getOwnerId() {
        return ownerId;
    }

    /**
     * @return true if the bot is using sharding.
     */
    public static boolean isSharding() {
        return useSharding;
    }

    /**
     * @return the total number of shards.
     */
    public static int getTotalShards() {
        return totalShards;
    }

    /**
     * @return the db's host name
     */
    public static String getHostName() {
        return hostName;
    }

    /**
     * @return the bot's default prefix.
     */
    public static String getPrefix() {
        return prefix;
    }

    /**
     * @param variable Config variable to get.
     * @return The value of the provided variable. Null if the provided variable doesn't exist.
     */
    private static String getValue(String variable) {
        String value = null;

        try {
            JsonNode configData = mapper.readTree(new File("./config.json"));

            value = configData.get(variable).asText();
        } catch (FileNotFoundException e) {
            logger.error("File \"config.json\" is not found within the directory.");

            generateConfigFile();
        } catch (IOException e) {
            logger.error("Failed to assign variable: {}", variable);
        }

        return value;
    }

    /**
     * Creates a .json file in the current directory containing config options.
     */
    private static void generateConfigFile() {
        logger.info("Generating config file from templates...");

        try {
            JsonNode templateFileAsTree = mapper.readTree(RenaConfig.class.getResourceAsStream("templates/config.json"));
            Object templateFileAsObject = mapper.treeToValue(templateFileAsTree, Object.class);

            mapper.writeValue(new File("config.json"), templateFileAsObject);

            logger.info("Successfully made a config file!");
            logger.info("Fill them up before executing again.");

            System.exit(0);
        } catch (IOException ignored) {
            logger.error("Failed to generate config file.");
        }
    }

}
