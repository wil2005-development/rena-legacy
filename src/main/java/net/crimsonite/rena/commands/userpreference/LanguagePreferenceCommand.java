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
		return 5;
	}

}
