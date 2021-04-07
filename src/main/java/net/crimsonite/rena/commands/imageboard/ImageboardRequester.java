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

package net.crimsonite.rena.commands.imageboard;

import java.awt.Color;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageboardRequester {
	
	private static ObjectMapper mapper = new ObjectMapper();
	private static OkHttpClient client = new OkHttpClient();
	
	protected static enum Imageboard {
		DANBOORU("Danbooru", true),
		SAFEBOORU("Safebooru", false);

		protected final String stringValue;
		protected final boolean isNsfw;
		
		Imageboard(String stringValue, boolean isNsfw) {
			this.stringValue = stringValue;
			this.isNsfw = isNsfw;
		}
		
	}
	
	/**
	 * @param imageboardType
	 * @param event -MessageReceivedEvent
	 * @param url -The URL of the imageboard API.
	 * @param tags -Tags to search.
	 * @return URL of the image
	 * @throws IOException
	 */
	protected static String getImage(Imageboard imageboardType, MessageReceivedEvent event, String url, String[] tags) throws IOException {	
		if (tags == null) {
			url = url.concat(".json");
		}
		else if (tags.length == 1) {
			url = url.concat(".json");
		}
		else {
			String urlFormat = ".json?tags=%1$s".formatted(tags[1]);
			
			if (!(tags.length == 2)) {
				urlFormat = ".json?tags=%1$s%2$s%3$s".formatted(tags[1], "%20", tags[2]);
			}
			
			url = url.concat(urlFormat);
		}
		
		// Only works for a premium imageboard account.
		/*
		else {
			url = url.concat(".json?tags=");
			
			for (int i = 1; i < tags.length; i++) {
				url = url.concat(tags[i] + "%20");
			}
			
			url = url.substring(0, url.length() - 3);
		}
		*/
		
		String imageUrl = null;
		
		Request request = new Request.Builder()
				.url(url)
				.build();
		
		Response response = client.newCall(request).execute();
		JsonNode jsonData = mapper.readValue(response.body().string(), JsonNode.class);
		
		if (!(imageboardType.isNsfw || jsonData.get("rating").asText() == "e")) {
			imageUrl = jsonData.get("file_url").asText();
		}
		else {
			imageUrl = I18n.getMessage(event.getAuthor().getId(), "imageboard.imageboard_requester.image_is_nsfw");
		}
		
		return imageUrl;
	}
	
	/**
	 * @param imageboardType
	 * @param event -MessageReceivedEvent
	 * @param url -The URL of the imageboard API.
	 * @return URL of the image
	 * @throws IOException
	 */
	protected static String getImage(Imageboard imageboardType, MessageReceivedEvent event, String url) throws IOException {
		return getImage(imageboardType, event, url, null);
	}
	
	/**
	 * @param imageboardType
	 * @param event -MessageReceivedEvent
	 * @param url -The URL of the imageboard API.
	 * @param tags -Tags to look for
	 * @return An embed of the imageboard result.
	 * @throws IOException
	 */
	protected static EmbedBuilder getEmbed(Imageboard imageboardType, MessageReceivedEvent event, String url, String[] tags) throws IOException {
		EmbedBuilder embed = null;
		User author = event.getAuthor();
		Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
		
		if (tags == null) {
			url = url.concat(".json");
		}
		else if (tags.length == 1) {
			url = url.concat(".json");
		}
		else {
			String urlFormat = ".json?tags=%1$s".formatted(tags[1]);
			
			if (!(tags.length == 2)) {
				urlFormat = ".json?tags=%1$s%2$s%3$s".formatted(tags[1], "%20", tags[2]);
			}
			
			url = url.concat(urlFormat);
		}
		
		// Only works for a premium imageboard account.
		/*
		else {
			url = url.concat(".json?tags=");
			
			for (int i = 1; i < tags.length; i++) {
				url = url.concat(tags[i] + "%20");
			}
			
			url = url.substring(0, url.length() - 3);
		}
		*/
		
		Request request = new Request.Builder()
				.url(url)
				.build();
		
		Response response = client.newCall(request).execute();
		JsonNode jsonValue = mapper.readValue(response.body().string(), JsonNode.class);
		
		if (!(imageboardType.isNsfw || jsonValue.get("rating").asText() == "e")) {
			embed = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle(imageboardType.stringValue, jsonValue.get("file_url").asText())
					.addField("Tags", "`%s`".formatted(jsonValue.get("tag_string").asText().replace(" ", "`, `")), false)
					.setImage(jsonValue.get("file_url").asText())
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());	
		}
		else {
			embed = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle(I18n.getMessage(event.getAuthor().getId(), "imageboard.imageboard_requester.image_is_nsfw"))
					.setImage("http://pm1.narvii.com/6291/40c2d61f7440a1dbb21d45f36571ceedf0899edf_00.jpg")
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
		}
		
		return embed;
	}
	
	/**
	 * @param imageboardType
	 * @param event -MessageReceivedEvent
	 * @param url -The URL of the imageboard API.
	 * @param tags -Tags to look for
	 * @return An embed of the imageboard result.
	 * @throws IOException
	 */
	protected static EmbedBuilder getEmbed(Imageboard imageboardType, MessageReceivedEvent event, String url) throws IOException {
		return getEmbed(imageboardType, event, url, null);
	}
	
}
