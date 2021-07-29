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

package net.crimsonite.rena.core;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;

public class I18n {

    private static final String defaultLanguage = "en";
    private static final String defaultCountry = "US";

    private static final Logger logger = LoggerFactory.getLogger(I18n.class);

    /**
     * Gets the string of message in localized form.
     *
     * @param language The locale's language.
     * @param country  The locale's country.
     * @param key      The id of the message string.
     * @return message in localized form.
     */
    public static String getMessage(String language, String country, String key) {
        String locale = "%1$s_%2$s".formatted(language, country);
        String message = "NULL";

        try {
            String[] keys = key.split("\\.");
            String key1;
            String key2;
            String key3;
            String messageKey;

            int keyLength = keys.length;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode resourceFile = mapper.readTree(I18n.class.getResourceAsStream("/languages/%s.json".formatted(locale)));

            switch (keyLength) {
                case 2 -> {
                    key1 = keys[0];
                    messageKey = keys[1];
                    message = resourceFile.get(key1).get(messageKey).asText();
                }
                case 3 -> {
                    key1 = keys[0];
                    key2 = keys[1];
                    messageKey = keys[2];
                    message = resourceFile.get(key1).get(key2).get(messageKey).asText();
                }
                case 4 -> {
                    key1 = keys[0];
                    key2 = keys[1];
                    key3 = keys[2];
                    messageKey = keys[3];
                    message = resourceFile.get(key1).get(key2).get(key3).get(messageKey).asText();
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            logger.warn("Invalid key sequence (%s)".formatted(key));
        } catch (IOException ignored) {
            logger.warn("Message not found.");
        }

        return message;
    }

    /**
     * Gets the string of message in localized form.
     *
     * @param user The UID of the user.
     * @param key  The key for message to look for.
     * @return message in localized form.
     */
    public static String getMessage(String user, String key) {
        String language = defaultLanguage;
        String country = defaultCountry;

        try {
            language = DBReadWrite.getValueString(Table.USERS, user, "Language");
            country = DBReadWrite.getValueString(Table.USERS, user, "Country");
        } catch (Exception | Error ignored) {
        }

        return getMessage(language, country, key);
    }

    /**
     * Gets the string of message in localized form.
     *
     * @param key The key for message to look for.
     * @return message in localized form.
     */
    public static String getMessage(String key) {
        return getMessage(defaultLanguage, defaultCountry, key);
    }

    /**
     * Gets a list of message in localized form.
     *
     * @param language The locale's language.
     * @param country  The locale's country.
     * @param key      The key for list to look for.
     * @return array of strings in localized form.
     */
    public static String[] getStringArray(String language, String country, String key) {
        String locale = "%s_%s".formatted(language, country);
        String[] list = new String[]{};

        try {
            String[] keys = key.split("\\.");
            String key1;
            String key2;
            String key3;
            String messageKey;

            int keyLength = keys.length;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode resourceFile = mapper.readTree(I18n.class.getResourceAsStream("/languages/%s.json".formatted(locale)));

            switch (keyLength) {
                case 2 -> {
                    key1 = keys[0];
                    messageKey = keys[1];
                    list = mapper.convertValue(resourceFile.get(key1).get(messageKey), String[].class);
                }
                case 3 -> {
                    key1 = keys[0];
                    key2 = keys[1];
                    messageKey = keys[2];
                    list = mapper.convertValue(resourceFile.get(key1).get(key2).get(messageKey), String[].class);
                }
                case 4 -> {
                    key1 = keys[0];
                    key2 = keys[1];
                    key3 = keys[2];
                    messageKey = keys[3];
                    list = mapper.convertValue(resourceFile.get(key1).get(key2).get(key3).get(messageKey), String[].class);
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            logger.warn("Invalid key sequence (%s)".formatted(key));
        } catch (IOException ignored) {
            logger.warn("Message not found.");
        }

        return list;
    }

    /**
     * Gets a list of message in localized form.
     *
     * @param user The UID of the user.
     * @param key  The key for list to look for.
     * @return array of strings in localized form.
     */
    public static String[] getStringArray(String user, String key) {
        String language = defaultLanguage;
        String country = defaultCountry;

        try {
            language = DBReadWrite.getValueString(Table.USERS, user, "Language");
            country = DBReadWrite.getValueString(Table.USERS, user, "Country");
        } catch (Exception | Error ignored) {
        }

        return getStringArray(language, country, key);
    }

    /**
     * Gets a list of message in localized form.
     *
     * @param key The key for list to look for.
     * @return array of strings in localized form.
     */
    public static String[] getStringArray(String key) {
        return getStringArray(defaultLanguage, defaultCountry, key);
    }
}
