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

package net.crimsonite.rena.commands.userpreference;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PreferenceCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		if (args.length >= 2) {
			switch (args[1]) {
				case "-set":
					if (args.length >= 3) {
						switch (args[2]) {
							case "language":
								// TODO put language_preference command here.
								
								break;
							default:
								channel.sendMessage(I18n.getMessage(author.getId(), "user_preference.preference.invalid_option")).queue();
								
								break;
						}
					}
					else {
						channel.sendMessage(I18n.getMessage(author.getId(), "user_preference.preference.no_option")).queue();
					}
					
					break;
				default:
					channel.sendMessage(I18n.getMessage(author.getId(), "user_preference.preference.invalid_action")).queue();
					
					break;
			}
		}
		else {
			channel.sendMessage(I18n.getMessage(author.getId(), "user_preference.preference.no_action")).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "preference";
	}

	@Override
	public String getCommandCategory() {
		return "Informations";
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
