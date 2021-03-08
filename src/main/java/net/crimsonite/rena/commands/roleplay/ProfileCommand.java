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

package net.crimsonite.rena.commands.roleplay;

import java.awt.Color;
import java.util.List;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ProfileCommand extends Command {
	
	private static void sendEmbed(MessageReceivedEvent event, User user) {
		Color roleColor = event.getGuild().retrieveMember(user).complete().getColor();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(user.getName() + "'s Profile")
				.setThumbnail(user.getEffectiveAvatarUrl())
				.addField("Rep", DBReadWrite.getValueString(Table.USERS, user.getId(), "REP"), false)
				.addField("Level", DBReadWrite.getValueString(Table.USERS, user.getId(), "LEVEL"), false)
				.addField("Exp", DBReadWrite.getValueString(Table.USERS, user.getId(), "EXP"), false)
				.addField("Money", DBReadWrite.getValueString(Table.USERS, user.getId(), "MONEY"), true)
				.addField("Hp", DBReadWrite.getValueString(Table.USERS, user.getId(), "HP"), true)
				.addField("Mp", DBReadWrite.getValueString(Table.USERS, user.getId(), "MP"), true)
				.addField("Atk", DBReadWrite.getValueString(Table.USERS, user.getId(), "ATK"), true)
				.addField("Def", DBReadWrite.getValueString(Table.USERS, user.getId(), "DEF"), true)
				.setFooter(event.getAuthor().getName(), user.getEffectiveAvatarUrl());
		
		event.getChannel().sendMessage(embed.build()).queue();
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		if (args.length == 1) {
			try {
				sendEmbed(event, event.getAuthor());
			}
			catch (NullPointerException ignored) {
				DBReadWrite.registerUser(event.getAuthor().getId());
				channel.sendMessage("Oops! Try again?").queue();
			}
		}
		else if (args.length >= 2) {
			if (!event.getMessage().getMentionedMembers().isEmpty()) {
				try {
					User user = event.getMessage().getMentionedUsers().get(0);
					sendEmbed(event, user);
				}
				catch (NullPointerException ignored) {
					DBReadWrite.registerUser(event.getMessage().getMentionedUsers().get(0).getId());
					channel.sendMessage("Oops! Try again?").queue();
				}
			}
			else {
				List<Member> listedMembers = FinderUtil.findMembers(args[1], event.getGuild());
				
				if (listedMembers.isEmpty()) {
					channel.sendMessage("*Err... I can't find that person. Try doing it again, I might've missed them*").queue();
					event.getGuild().loadMembers();
				}
				else {
					try {
						User user = listedMembers.get(0).getUser();
						sendEmbed(event, user);
					}
					catch (NullPointerException ignored) {
						DBReadWrite.registerUser(listedMembers.get(0).getId());
						channel.sendMessage("Oops! Try again?").queue();
					}
				}
			}
		}
	}

	@Override
	public String getCommandName() {
		return "profile";
	}

	@Override
	public long cooldown() {
		return 5;
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

}
