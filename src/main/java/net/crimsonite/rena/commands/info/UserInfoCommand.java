/*
 * Copyright (C) 2020  Nhalrath
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
import net.dv8tion.jda.api.entities.User;

@CommandInfo(
		name = {"profile"},
		description = "Shows the author's profile/member's if specified."
		)
public class UserInfoCommand extends Command{
	
	private static User user;
	private static User author;
	private static Color roleColor;
	
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	public UserInfoCommand() {
		this.name = "profile";
		this.aliases = new String[] {"iam", "userinfo"};
		this.category = new Category("Informations");
		this.help = "Shows the author's profile/member's if specified.";
		this.arguments = "[mention]";
		this.guildOnly = true;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		if (event.getAuthor().isBot())
			return;
		
		if (event.getArgs().isEmpty()) {
			author = event.getAuthor();
			roleColor = event.getGuild().retrieveMember(author).complete().getColor();
			
			EmbedBuilder embed = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle(author.getName() + "'s Profile")
					.setThumbnail(author.getEffectiveAvatarUrl())
					.addField("ID", author.getId(), false)
					.addField("Date Created", author.getTimeCreated().format(format), false)
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			event.reply(embed.build());
		}
		else {
			author = event.getAuthor();
			
			try {
				user = event.getMessage().getMentionedUsers().get(0);
				roleColor = event.getGuild().retrieveMember(user).complete().getColor();
				
				EmbedBuilder embed = new EmbedBuilder()
						.setColor(roleColor)
						.setTitle(user.getName() + "'s Profile")
						.setThumbnail(user.getEffectiveAvatarUrl())
						.addField("ID", user.getId(), false)
						.addField("Date Created", user.getTimeCreated().format(format), false)
						.setFooter(author.getName(), author.getEffectiveAvatarUrl());
				
				event.reply(embed.build());
			}
			catch (IndexOutOfBoundsException e) {
				event.reply("Please mention a user.");
			}
		}
		
	}
	
}
