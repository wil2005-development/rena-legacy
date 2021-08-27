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

import net.crimsonite.rena.core.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageboardRequester {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final OkHttpClient client = new OkHttpClient();

    protected enum Imageboard {
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
     * @param imageboardType Imageboard type.
     * @param event          GuildMessageReceivedEvent.
     * @param url            The URL of the imageboard API.
     * @param tags           Tags to search.
     * @return URL of the image
     * @throws IOException          Something went wrong while communicating with the REST API.
     * @throws NullPointerException The response body is empty.
     */
    protected static String getImage(Imageboard imageboardType, GuildMessageReceivedEvent event, String url, String[] tags) throws IOException, NullPointerException {
        if (tags == null || tags.length == 1) {
            url = url.concat(".json");
        } else {
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

        String imageUrl;

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        if (response.body() == null) throw new NullPointerException();

        JsonNode jsonData = mapper.readValue(response.body().string(), JsonNode.class);

        if (!(imageboardType.isNsfw || jsonData.get("rating").asText().equals("e"))) {
            imageUrl = jsonData.get("file_url").asText();
        } else {
            imageUrl = I18n.getMessage(event.getAuthor().getId(), "imageboard.imageboard_requester.image_is_nsfw");
        }

        return imageUrl;
    }

    /**
     * @param imageboardType Imageboard type.
     * @param event          GuildMessageReceivedEvent.
     * @param url            The URL of the imageboard API.
     * @return URL of the image.
     * @throws IOException          Something went wrong while communicating with the REST API.
     * @throws NullPointerException The response body is empty.
     */
    protected static String getImage(Imageboard imageboardType, GuildMessageReceivedEvent event, String url) throws IOException, NullPointerException {
        return getImage(imageboardType, event, url, null);
    }

    /**
     * @param imageboardType Imageboard type.
     * @param event          GuildMessageReceivedEvent.
     * @param url            The URL of the imageboard API.
     * @param tags           Tags to look for.
     * @return An embed of the imageboard result.
     * @throws IOException          Something went wrong while communicating with the REST API.
     * @throws NullPointerException The response body is empty.
     */
    protected static EmbedBuilder getEmbed(Imageboard imageboardType, GuildMessageReceivedEvent event, String url, String[] tags) throws IOException, NullPointerException {
        EmbedBuilder embed;
        User author = event.getAuthor();
        Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();

        if (tags == null || tags.length == 1) {
            url = url.concat(".json");
        } else {
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

        if (response.body() == null) throw new NullPointerException();

        JsonNode jsonValue = mapper.readValue(response.body().string(), JsonNode.class);

        if (!(imageboardType.isNsfw || jsonValue.get("rating").asText().equals("e"))) {
            embed = new EmbedBuilder()
                    .setColor(roleColor)
                    .setTitle(imageboardType.stringValue, jsonValue.get("file_url").asText())
                    .addField("Tags", "`%s`".formatted(jsonValue.get("tag_string").asText().replace(" ", "`, `")), false)
                    .setImage(jsonValue.get("file_url").asText())
                    .setFooter(author.getName(), author.getEffectiveAvatarUrl());
        } else {
            embed = new EmbedBuilder()
                    .setColor(roleColor)
                    .setTitle(I18n.getMessage(event.getAuthor().getId(), "imageboard.imageboard_requester.image_is_nsfw"))
                    .setImage("http://pm1.narvii.com/6291/40c2d61f7440a1dbb21d45f36571ceedf0899edf_00.jpg")
                    .setFooter(author.getName(), author.getEffectiveAvatarUrl());
        }

        return embed;
    }

    /**
     * @param imageboardType Imageboard type.
     * @param event          GuildMessageReceivedEvent.
     * @param url            The URL of the imageboard API.
     * @return An embed of the imageboard result.
     * @throws IOException          Something went wrong while communicating with the REST API.
     * @throws NullPointerException The response body is empty.
     */
    protected static EmbedBuilder getEmbed(Imageboard imageboardType, GuildMessageReceivedEvent event, String url) throws IOException, NullPointerException {
        return getEmbed(imageboardType, event, url, null);
    }

}
