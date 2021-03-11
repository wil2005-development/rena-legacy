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
	
	public static String getMessage(String language, String country, String context) {
		String locale = "%s_%s".formatted(language, country);
		String message = "NULL";
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JsonNode resourceFile = mapper.readTree(I18n.class.getResourceAsStream("/languages/%s.json".formatted(locale)));
			
			message = resourceFile.get(context).asText();
		} catch (IOException ignored) {
			logger.warn("Message not found");
		}
		
		return message;
	}
	
	public static String getMessage(String context) {
		return getMessage(defaultLanguage, defaultCountry, context);
	}
}
