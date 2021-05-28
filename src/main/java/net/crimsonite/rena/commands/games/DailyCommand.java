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

package net.crimsonite.rena.commands.games;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DailyCommand extends Command{

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		User recepient = author;
		MessageChannel channel = event.getChannel();
		
		try {
			long lastClaim = DBReadWrite.getValueInt(Table.PLAYERS, author.getId(), "LAST_DAILY_CLAIM");
			long currentTime = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());
			int currentTimeInInteger = Integer.parseInt(String.valueOf(currentTime));
			
			String dailyStreakDialogue;
			
			if (args.length >= 2) {
				if (!event.getMessage().getMentionedMembers().isEmpty()) {
					recepient = event.getMessage().getMentionedMembers().get(0).getUser();
				}
				else {
					List<Member> listedMembers = FinderUtil.findMembers(args[1], event.getGuild());
					
					if (listedMembers.isEmpty()) {
						channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "roleplay.daily.user_not_found")).queue();
						event.getGuild().loadMembers();
					}
					else {
						recepient = listedMembers.get(0).getUser();
					}
				}
			}
			
			if (currentTime < (lastClaim + 24)) {
				DBReadWrite.incrementValue(Table.PLAYERS, author.getId(), "DAILY_STREAK", 1);
				
				dailyStreakDialogue = I18n.getMessage("roleplay.daily.daily_streak_increment");
			}
			else {
				DBReadWrite.modifyDataInt(Table.PLAYERS, author.getId(), "DAILY_STREAK", 0);
				
				dailyStreakDialogue = I18n.getMessage("roleplay.daily.daily_streak_reset");
			}
			
			long dailyStreak = DBReadWrite.getValueInt(Table.PLAYERS, author.getId(), "DAILY_STREAK");
			long level = DBReadWrite.getValueInt(Table.PLAYERS, author.getId(), "LEVEL");
			long reward = (50 + dailyStreak) * level;
			
			DBReadWrite.incrementValue(Table.PLAYERS, recepient.getId(), "MONEY", Integer.parseInt(String.valueOf(reward)));
			DBReadWrite.modifyDataInt(Table.PLAYERS, author.getId(), "LAST_DAILY_CLAIM", currentTimeInInteger);
			
			channel.sendMessageFormat(I18n.getMessage(event.getAuthor().getId(), "roleplay.daily.claimed"), reward).queue();
			channel.sendMessage(dailyStreakDialogue.formatted(dailyStreak)).queue();
		}
		catch (NullPointerException e) {
			DBReadWrite.registerUser(author.getId());
			
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "roleplay.daily.not_registered")).queue();
		}
		catch (IllegalArgumentException e) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "roleplay.daily.not_qualified")).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "daily";
	}
	
	@Override
	public String getCommandCategory() {
		return "Games";
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

	@Override
	public long cooldown() {
		return 86400;
	}

}
