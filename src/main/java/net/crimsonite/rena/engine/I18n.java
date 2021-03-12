package net.crimsonite.rena.engine;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
			String category;
			String context;
			String messageKey;
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode resourceFile = mapper.readTree(I18n.class.getResourceAsStream("/languages/%s.json".formatted(locale)));
			
			if (keys.length == 2) {
				context = keys[0];
				messageKey = keys[1];
				
				message = resourceFile.get(context).get(messageKey).asText();
			}
			else if (keys.length == 3) {
				category = keys[0];
				context = keys[1];
				messageKey = keys[2];
				
				message = resourceFile.get(category).get(context).get(messageKey).asText();
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
	 * @param key -The key for message to look for.
	 * @return message
	 */
	public static String getMessage(String key) {
		return getMessage(defaultLanguage, defaultCountry, key);
	}
}
