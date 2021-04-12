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

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DailyCommand extends Command{

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		try {
			long level = DBReadWrite.getValueInt(Table.PLAYERS, author.getId(), "LEVEL");
			long reward = 50 * level;
			
			DBReadWrite.incrementValue(Table.PLAYERS, author.getId(), "MONEY", Integer.parseInt(String.valueOf(reward)));
			
			channel.sendMessageFormat(I18n.getMessage(event.getAuthor().getId(), "roleplay.daily.claimed"), reward).queue();
		}
		catch (NullPointerException ignored) {
			DBReadWrite.registerUser(author.getId());
			
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "roleplay.daily.error")).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "daily";
	}
	
	@Override
	public String getCommandCategory() {
		return "Roleplay";
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
