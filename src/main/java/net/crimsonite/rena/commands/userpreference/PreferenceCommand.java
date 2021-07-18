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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PreferenceCommand extends Command {
	
	private void setLanguage(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		try {
			boolean combinationCheck = false;
			
			String language = "en";
			String country = "US";
			String line;
			
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("languages/languages.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			List<String> validCombinations = new ArrayList<>();
			
			while ((line = reader.readLine()) != null) {
				validCombinations.add(line);
			}
			
			for (String combination : validCombinations) {
				if (args[1].contains(combination)) {
					String[] values = args[1].split("\\_");
					
					language = values[0];
					country = values[1];
					
					combinationCheck = true;
				}
			}
			
			if (combinationCheck == true) {
				DBReadWrite.modifyDataString(Table.USERS, author.getId(), "Language", language);
				DBReadWrite.modifyDataString(Table.USERS, author.getId(), "Country", country);
				
				channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.set_lang_success").formatted(language, country)).queue();
			}
			else {
				channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.set_lang_failed")).queue();
			}
		}
		catch (Exception e) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.error")).queue();
		}
	}

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
								setLanguage(event, args);
								
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

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return null;
	}

}
