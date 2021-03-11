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

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.crimsonite.rena.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UserinfoCommand extends Command {
	
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	private static void sendEmbed(MessageReceivedEvent event, Member member) {
		User memberAsUser = member.getUser();
		Member author = event.getMember();
		Color roleColor = event.getGuild().retrieveMember(memberAsUser).complete().getColor();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(member.getEffectiveName() + "'s User Info")
				.setThumbnail(memberAsUser.getEffectiveAvatarUrl())
				.addField("ID", member.getId(), false)
				.addField("Date Created", member.getTimeCreated().format(format), false)
				.addField("Date Joined", member.getTimeJoined().format(format),false)
				.setFooter(author.getEffectiveName(), author.getUser().getEffectiveAvatarUrl());
		
		event.getChannel().sendMessage(embed.build()).queue();
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		Member author = event.getMember();
		
		if (args.length == 1) {
			sendEmbed(event, author);
		}
		else if (args.length >= 2){
			if (!event.getMessage().getMentionedMembers().isEmpty()) {
				Member member = event.getMessage().getMentionedMembers().get(0);
				sendEmbed(event, member);
			}
			else {
				List<Member> listedMembers = FinderUtil.findMembers(args[1], event.getGuild());
				
				if (listedMembers.isEmpty()) {
					channel.sendMessage("*Err... I can't find that person. Try doing it again, I might've missed them*").queue();
					event.getGuild().loadMembers();
				}
				else {
					Member member = listedMembers.get(0);
					sendEmbed(event, member);
				}
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
