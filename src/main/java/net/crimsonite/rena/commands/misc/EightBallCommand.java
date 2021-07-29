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

public class EightBallCommand extends Command {

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        MessageChannel channel = event.getChannel();

        String[] possibleResponses = I18n.getStringArray(event.getAuthor().getId(), "misc.8ball.answers");
        String answer = possibleResponses[RandomGenerator.randomInt(possibleResponses.length, RandomGenerator.generateSeedFromCurrentTime())];

        if (args.length == 1) {
            channel.sendMessage(I18n.getMessage("misc.8ball.no_question")).queue();
        } else {
            channel.sendMessage(answer).reference(event.getMessage()).mentionRepliedUser(false).queue();
        }
    }

    @Override
    public String getCommandName() {
        return "8ball";
    }

    @Override
    public String getCommandCategory() {
        return "Miscellaneous";
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
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

}
