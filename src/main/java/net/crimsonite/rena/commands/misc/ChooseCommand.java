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
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;

public class ChooseCommand extends Command {


    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        User author = event.getAuthor();
        MessageChannel channel = event.getChannel();

        if (args.length > 2) {
            String picked = "";

            for (String arg : args) {
                String[] choices;
                StringBuilder stringBuilder = new StringBuilder();

                if (!Objects.equals(arg, args[0])) {
                    stringBuilder.append(arg);
                }

                choices = stringBuilder.toString().split(", ");

                picked = choices[RandomGenerator.randomInt(
                        choices.length,
                        RandomGenerator.generateSeedFromCurrentTime())];
            }

            channel.sendMessage(I18n.getMessage(author.getId(), "misc.choose.picked").formatted(picked)).queue();
        } else if (args.length == 2) {
            channel.sendMessage(I18n.getMessage(author.getId(), "misc.choose.only_one_choice")).queue();
        } else {
            channel.sendMessage(I18n.getMessage(author.getId(), "misc.choose.no_provided_choices")).queue();
        }
    }

    @Override
    public String getCommandName() {
        return "choose";
    }

    @Override
    public String getCommandCategory() {
        return "Miscellaneous";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
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