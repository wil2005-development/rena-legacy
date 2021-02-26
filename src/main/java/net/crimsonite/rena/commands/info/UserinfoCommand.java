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
import java.util.List;
import java.util.ListIterator;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.crimsonite.rena.utils.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.concurrent.Task;

public class UserinfoCommand extends Command {
	
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		if (args.length == 1) {
			User author = event.getAuthor();
			Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
			EmbedBuilder embed = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle(author.getName() + "'s User Info")
					.setThumbnail(author.getEffectiveAvatarUrl())
					.addField("ID", author.getId(), false)
					.addField("Date Created", author.getTimeCreated().format(format), false)
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			channel.sendMessage(embed.build()).queue();
		}
		else {
			List<Member> listedMembers = FinderUtil.findMembers(args[1], event.getGuild());
			
			if (listedMembers.isEmpty()) {
				channel.sendMessage("*Err... I can't find that person. Try doing it again, I might've missed them*").queue();
				event.getGuild().loadMembers();
			}
			else {
				User user = listedMembers.get(0).getUser();
				Color roleColor = event.getGuild().retrieveMember(user).complete().getColor();
				EmbedBuilder embed = new EmbedBuilder()
						.setColor(roleColor)
						.setTitle(user.getName() + "'s User Info")
						.setThumbnail(user.getEffectiveAvatarUrl())
						.addField("ID", user.getId(), false)
						.addField("Date Created", user.getTimeCreated().format(format), false)
						.setFooter(user.getName(), user.getEffectiveAvatarUrl());
				
				channel.sendMessage(embed.build()).queue();
			}
		}
	}

	@Override
	public String getCommandName() {
		return "userinfo";
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

	@Override
	public long cooldown() {
		return 5;
	}

}
