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

import java.util.List;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class BanCommand extends Command {

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        Member author = event.getMember();
        MessageChannel channel = event.getChannel();

        if (mentionedMembers.isEmpty()) {
            channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "moderation.ban.no_mentioned_member").formatted(":warning:")).queue();
        } else if (mentionedMembers.size() > 1) {
            channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "moderation.ban.multiple_mentions").formatted(":warning:")).queue();
        } else {
            if (author.hasPermission(Permission.BAN_MEMBERS)) {
                try {
                    if (args.length > 2) {
                        String reason = args[2];

                        mentionedMembers.get(0).ban(0, reason).complete();
                        channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "moderation.ban.ban_with_reason_success").formatted(":white_check_mark:", mentionedMembers.get(0).getUser().getName(), reason)).queue();
                    } else {
                        mentionedMembers.get(0).ban(0).complete();
                        channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "moderation.ban.ban_success").formatted(":white_check_mark:", mentionedMembers.get(0).getUser().getName())).queue();
                    }
                } catch (HierarchyException ignored) {
                    channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "moderation.ban.unable_to_ban").formatted("warning")).queue();
                }
            } else {
                channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "moderation.ban.no_permission").formatted(":warning:")).queue();
            }
        }
    }

    @Override
    public String getCommandName() {
        return "ban";
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
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

}
