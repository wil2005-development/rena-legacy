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
import net.crimsonite.rena.core.Cooldown;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PreferenceCommand extends Command {

    private boolean shouldRemoveCooldown = false;

    private String playerId;

    private void setLanguage(GuildMessageReceivedEvent event, String[] args) {
        User author = event.getAuthor();
        MessageChannel channel = event.getChannel();
        List<String> validLanguages = new ArrayList<>();

        this.playerId = author.getId();

        try {
            String[] localeArgs = args[3].split("\\_");

            if (localeArgs.length > 2) {
                channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.improper_format").formatted(args[3])).queue();

                return;
            }

            String language = localeArgs[0];
            String country = localeArgs[1];
            String countryCode = "%1$s_%2$s".formatted(language, country);

            String validLanguagesText;
            String line;

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("languages/languages.txt");

            if (inputStream == null) throw new NullPointerException();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                validLanguages.add(line);
            }

            for (String lang : validLanguages) {
                stringBuilder.append(lang);
                stringBuilder.append(", ");
            }

            validLanguagesText = stringBuilder.toString();

            if (validLanguages.contains(countryCode)) {
                DBReadWrite.modifyDataString(Table.USERS, author.getId(), "Language", language);
                DBReadWrite.modifyDataString(Table.USERS, author.getId(), "Country", country);

                channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.set_lang_success").formatted(countryCode)).queue();
            } else {
                channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.invalid_language").formatted(countryCode, validLanguagesText.substring(0, (validLanguagesText.length() - 2)))).queue();

                this.shouldRemoveCooldown = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "user_preference.language_preference.set_lang_failed")).queue();

            this.shouldRemoveCooldown = true;
        }
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        User author = event.getAuthor();
        MessageChannel channel = event.getChannel();

        String action = args[1];
        String option = args[2];

        if (args.length >= 2) {
            switch (action) {
                case "-set":
                    if (args.length >= 3) {
                        switch (option) {
                            case "language":
                                setLanguage(event, args);

                                break;
                            default:
                                channel.sendMessage(I18n.getMessage(author.getId(), "user_preference.preference.invalid_option")).queue();

                                break;
                        }
                    } else {
                        channel.sendMessage(I18n.getMessage(author.getId(), "user_preference.preference.no_option")).queue();

                        this.shouldRemoveCooldown = true;
                    }
                    break;
                default:
                    channel.sendMessage(I18n.getMessage(author.getId(), "user_preference.preference.invalid_action")).queue();

                    this.shouldRemoveCooldown = true;

                    break;
            }
        } else {
            channel.sendMessage(I18n.getMessage(author.getId(), "user_preference.preference.no_action")).queue();

            this.shouldRemoveCooldown = true;
        }
    }

    @Override
    public void postCommandEvent() {
        if (this.shouldRemoveCooldown) {
            Cooldown.removeCooldown(this.playerId, getCommandName());
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
        return 60;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

}
