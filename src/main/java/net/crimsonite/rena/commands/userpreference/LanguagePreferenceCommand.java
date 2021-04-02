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
import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LanguagePreferenceCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		try {
			boolean languageCheck = false;
			boolean countryCheck = false;
			
			String language = "en";
			String country = "US";
			String[] supportedLanguages = {"en", "fil"};
			String[] supportedCountries = {"US", "PH"};
			
			for (String lang : supportedLanguages) {
				if (args[1].contains(lang)) {
					language = args[1];
					languageCheck = true;
				}
			}
			
			for (String countryItem : supportedCountries) {
				if (args[2].contains(countryItem)) {
					country = args[2];
					countryCheck = true;
				}
			}
			
			if (languageCheck && countryCheck == true) {
				DBReadWrite.modifyDataString(Table.USERS, author.getId(), "Language", language);
				DBReadWrite.modifyDataString(Table.USERS, author.getId(), "Country", country);
				
				channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.set_lang_success").formatted(language, country)).queue();
			}
			else {
				channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.set_lang_failed")).queue();
			}
		}
		catch (ArrayIndexOutOfBoundsException ignored) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.error")).queue();
		}
		catch (IllegalArgumentException ignored) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.error")).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "set_lang";
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

	@Override
	public long cooldown() {
		return 60;
	}

}
