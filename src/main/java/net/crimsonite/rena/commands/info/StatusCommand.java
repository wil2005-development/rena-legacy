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

package net.crimsonite.rena.commands.info;

import java.awt.Color;
import java.time.format.DateTimeFormatter;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

import net.crimsonite.rena.RenaInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.User;

@CommandInfo(
		name = {"status"},
		description = "Shows the information about the bot."
		)
public class StatusCommand extends Command{
	
	public static User author;
	public static Color roleColor;
	
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	public StatusCommand() {
		this.name = "status";
		this.aliases = new String[] {"botinfo"};
		this.category = new Category("Informations");
		this.help = "Shows the information about the bot.";
		this.guildOnly = false;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		if (event.getAuthor().isBot())
			return;
		
		author = event.getAuthor();
		roleColor = event.getGuild().getMember(author).getColor();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(event.getSelfUser().getName() + "'s Informations", RenaInfo.GITHUB_URL)
				.setThumbnail(event.getSelfUser().getEffectiveAvatarUrl())
				.addField("ID", event.getSelfUser().getId(), true)
				.addField("Date Created", event.getSelfUser().getTimeCreated().format(format), false)
				.addField("Version", RenaInfo.VERSION_STRING, false)
				.addField("Libraries Used", "JDA " + JDAInfo.VERSION + "\n" + "JDA-Utils " + JDAUtilitiesInfo.VERSION + "\n", false)
				.addField("Shard", event.getJDA().getShardInfo().getShardString(), true)
				.addField("Guilds", event.getJDA().getGuilds().size()+"", true)
				.addField("Users", event.getJDA().getUsers().size()+"", true)
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
		
		event.reply(embed.build());
		
	}

}
