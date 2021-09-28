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

package net.crimsonite.rena.commands.moderation;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetGuildPrefixCommand extends Command {

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        Member author = event.getMember();
        MessageChannel channel = event.getChannel();

        String prefix;

        try {
            prefix = args[1];
        } catch (IndexOutOfBoundsException ignored) {
            channel.sendMessage(I18n.getMessage(author.getId(), "moderation.guild_prefix.failed").formatted(":warning:")).queue();

            return;
        }

        if (author.hasPermission(Permission.ADMINISTRATOR)) {
            try {
                DBReadWrite.getValueString(Table.GUILDS, event.getGuild().getId(), "Prefix");
                DBReadWrite.modifyDataString(Table.GUILDS, event.getGuild().getId(), "Prefix", prefix);

                channel.sendMessage(I18n.getMessage(author.getId(), "moderation.guild_prefix.success").formatted(":white_check_mark:", prefix)).queue();
            } catch (NullPointerException ignored) {
                DBReadWrite.registerGuild(event.getGuild().getId());

                channel.sendMessage(I18n.getMessage(author.getId(), "common_string.late_registration")).queue();
            }
        } else {
            channel.sendMessage(I18n.getMessage(author.getId(), "moderation.guild_prefix.no_permission").formatted(":warning:")).queue();
        }
    }

    @Override
    public String getCommandName() {
        return "set_prefix";
    }

    @Override
    public String getCommandCategory() {
        return "Moderation";
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
