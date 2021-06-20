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

package net.crimsonite.rena.commands.misc;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.utils.RandomGenerator;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DiceCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		if (args.length == 2) {
			try {
				String[] die = args[1].split("d");
				int numberOfDice = Integer.parseInt(die[0]);
				int face = Integer.parseInt(die[1]);
				int result = 0;
				
				for (int i = 0; i < numberOfDice; i++) {
					result += RandomGenerator.randomInt(1, face);
				}
				
				channel.sendMessageFormat(":game_die: %d (1-%d)", result, face * numberOfDice).queue();
			}
			catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
				channel.sendMessageFormat(I18n.getMessage(event.getAuthor().getId(), "misc.dice.unable_to_roll")).queue();
			}
		}
		else {
			int result = RandomGenerator.randomInt(1, 6);
			channel.sendMessageFormat(":game_die: %d (1-6)", result).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "roll";
	}
	
	@Override
	public String getCommandCategory() {
		return "Miscellaneous";
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
