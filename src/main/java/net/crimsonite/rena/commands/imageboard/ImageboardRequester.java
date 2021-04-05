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
	
	protected static class Unfiltered {
		
		private static ObjectMapper mapper = new ObjectMapper();
		private static OkHttpClient client = new OkHttpClient();
		
		/**
		 * @param url -The URL of the imageboard API.
		 * @return The URL of the raw image
		 * @throws IOException
		 */
		protected static String getUnfilteredImage(Imageboard imageboardType, String url, MessageReceivedEvent event) throws IOException {
			url = url.concat(".json");
			
			String imageUrl = null;
			
			Request request = new Request.Builder()
					.url(url)
					.build();
			
			Response response = client.newCall(request).execute();
			
			if (!imageboardType.isNsfw) {
				imageUrl = mapper.readValue(response.body().string(), JsonNode.class).get("file_url").asText();
			}
			else {
				imageUrl = I18n.getMessage(event.getAuthor().getId(), "imageboard.imageboard_requester.image_is_nsfw");
			}
			
			return imageUrl;
		}
		
		/**
		 * @param imageboardType
		 * @param event
		 * @param url -The URL of the imageboard API
		 * @return An embed of the imageboard result.
		 * @throws IOException
		 */
		protected static EmbedBuilder getUnfilteredEmbed(Imageboard imageboardType, MessageReceivedEvent event, String url) throws IOException {
			EmbedBuilder embed = null;
			User author = event.getAuthor();
			Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
			
			url = url.concat(".json");
			
			Request request = new Request.Builder()
					.url(url)
					.build();
			
			Response response = client.newCall(request).execute();
			JsonNode jsonValue = mapper.readValue(response.body().string(), JsonNode.class);
			
			if (!imageboardType.isNsfw) {
				embed = new EmbedBuilder()
						.setColor(roleColor)
						.setTitle(imageboardType.stringValue, jsonValue.get("file_url").asText())
						.addField("Tags", "`%s`".formatted(jsonValue.get("tag_string_general").asText().replace(" ", "`, `")), false)
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
		
	}
	
}
