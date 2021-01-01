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
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@CommandInfo(
		name = {"guildinfo"},
		description = "Shows the informations about the guild."
		)
public class GuildInfoCommand extends Command{
	
	private static Guild guild;
	private static User author;
	private static Color roleColor;
	
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	public GuildInfoCommand() {
		this.name = "guild";
		this.aliases = new String[] {"guildinfo, serverinfo"};
		this.category = new Category("Informations");
		this.help = "Shows the informations about the guild.";
		this.guildOnly = false;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		guild = event.getGuild();
		author = event.getAuthor();
		roleColor = event.getGuild().getMember(author).getColor();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle("Showing informations for " + guild.getName())
				.setThumbnail(guild.getIconUrl())
				.addField("ID", guild.getId(), false)
				.addField("Date Created", guild.getTimeCreated().format(format), false)
				.addField("Owner", guild.getOwner().getEffectiveName(), false)
				.addField("Members", "" + guild.getMemberCount(), true)
				.addField("Roles", "" + guild.getRoles().size(), true)
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
				
		event.reply(embed.build());
	}

}
