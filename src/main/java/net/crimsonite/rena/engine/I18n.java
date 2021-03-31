package net.crimsonite.rena.engine;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;

public class I18n {
	
	private static String defaultLanguage = "en";
	private static String defaultCountry = "US";
	
	private static final Logger logger = LoggerFactory.getLogger(I18n.class);
	
	/**
	 * @param language
	 * @param country
	 * @param key -The key for message to look for.
	 * @return message
	 */
	public static String getMessage(String language, String country, String key) {
		String locale = "%s_%s".formatted(language, country);
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
				case 2:
					key1 = keys[0];
					messageKey = keys[1];
					
					message = resourceFile.get(key1).get(messageKey).asText();
					
					break;
				case 3:
					key1 = keys[0];
					key2 = keys[1];
					messageKey = keys[2];
					
					message = resourceFile.get(key1).get(key2).get(messageKey).asText();
					
					break;
				case 4:
					key1 = keys[0];
					key2 = keys[1];
					key3 = keys[2];
					messageKey = keys[3];
					
					message = resourceFile.get(key1).get(key2).get(key3).get(messageKey).asText();
					
					break;
			}
		}
		catch (ArrayIndexOutOfBoundsException ignored) {
			logger.warn("Invalid key sequence (%s)".formatted(key));
		}
		catch (IOException ignored) {
			logger.warn("Message not found.");
		}
		
		return message;
	}
	
	/**
	 * @param user -The UID of the user.
	 * @param key -The key for message to look for.
	 * @return
	 */
	public static String getMessage(String user, String key) {
		String language = defaultLanguage;
		String country = defaultCountry;
		
		try {
			language = DBReadWrite.getValueString(Table.USERS, user, "language");
			country = DBReadWrite.getValueString(Table.USERS, user, "country");
		}
		catch (NullPointerException ignored) {}
		
		return getMessage(language, country, key);
	}
	
	/**
	 * @param key -The key for message to look for.
	 * @return message
	 */
	public static String getMessage(String key) {
		return getMessage(defaultLanguage, defaultCountry, key);
	}
}
