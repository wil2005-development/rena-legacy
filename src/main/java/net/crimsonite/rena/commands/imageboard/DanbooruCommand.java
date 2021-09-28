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

package net.crimsonite.rena.commands.imageboard;

import java.io.IOException;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.commands.imageboard.ImageboardRequester.Imageboard;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DanbooruCommand extends Command {

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        EmbedBuilder embed;

        try {
            embed = ImageboardRequester.getEmbed(Imageboard.DANBOORU, event, "https://danbooru.donmai.us/posts/random", args);
        } catch (IOException ignored) {
            embed = new EmbedBuilder()
                    .setTitle("An error has occurred");
        }

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public String getCommandName() {
        return "danbooru";
    }

    @Override
    public String getCommandCategory() {
        return "Imageboard";
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
