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

package net.crimsonite.rena.commands.dev;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ModifyAttributesCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		Table table = null;
		
		String message = "```diff\n+SUCCESS: [%s] Operation executed successfully!```";
		
		try {
			switch (args[5]) {
				case "USERS":
					table = Table.USERS;
					
					break;
				case "PLAYERS":
					table = Table.PLAYERS;
					
					break;
				case "GUILDS":
					table = Table.GUILDS;
					
					break;
				default:
					channel.sendMessage("```diff\n-ERROR: Table not specified.```").queue();
					
					break;
			}
			switch (args[1]) {
				case "BOOLEAN":
					DBReadWrite.modifyDataBoolean(table, args[2], args[3], Boolean.parseBoolean(args[4]));
					channel.sendMessageFormat(message, args[1]).queue();
					
					break;
				case "INT":
					DBReadWrite.modifyDataInt(table, args[2], args[3], Integer.parseInt(args[4]));
					channel.sendMessageFormat(message, args[1]).queue();
					
					break;
				case "INT_INCREMENT":
					DBReadWrite.incrementValue(table, args[2], args[3], Integer.parseInt(args[4]));
					channel.sendMessageFormat(message, args[1]).queue();
					
					break;
				case "INT_DECREMENT":
					DBReadWrite.decrementValue(table, args[2], args[3], Integer.parseInt(args[4]));
					channel.sendMessageFormat(message, args[1]).queue();
					
					break;
				case "STRING":
					DBReadWrite.modifyDataString(table, args[2], args[3], args[4]);
					channel.sendMessageFormat(message, args[1]).queue();
					
					break;
				default:
					channel.sendMessage("```diff\n-ERROR: Invalid Argument```").queue();
					
					break;
			}
		}
		catch (ArrayIndexOutOfBoundsException ignored) {
			channel.sendMessage("```diff\n-ERROR: Received no Arguments```").queue();
		}
		catch (IllegalArgumentException ignored) {
			channel.sendMessage("```diff\n-ERROR: Received an Illegal Argument```").queue();
		}
		catch (NullPointerException ignored) {
			channel.sendMessage("```diff\n-ERROR: Operation returned a null value```").queue();
		}
	}

	@Override
	public String getCommandName() {
		return "modify";
	}

	@Override
	public long cooldown() {
		return 0;
	}

	@Override
	public boolean isOwnerCommand() {
		return true;
	}

}
