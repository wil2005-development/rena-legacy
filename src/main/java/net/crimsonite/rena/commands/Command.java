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

package net.crimsonite.rena.commands;

import java.util.concurrent.TimeUnit;

import net.crimsonite.rena.RenaConfig;
import net.crimsonite.rena.core.Cooldown;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter {

    public abstract void execute(MessageReceivedEvent event, String[] args);

    public abstract String getCommandName();

    public abstract String getCommandCategory();

    public abstract String getHelp();

    public abstract String getUsage();

    public abstract boolean isOwnerCommand();

    public abstract long cooldown();

    private static long timesCommandUsed = 0;

    /**
     * @return Number of times the command was called.
     */
    public static long getTimesCommandUsed() {
        return timesCommandUsed;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        MessageChannel channel = event.getChannel();

        if (event.getChannelType() != ChannelType.TEXT) return;

        if (author.isBot()) return;

        if (isOwnerCommand()) {
            if (author.getIdLong() != RenaConfig.getOwnerId()) return;
        }

        if (containsCommand(event.getMessage(), event)) {
            String command = getCommandName();

            if (Cooldown.getCooldownCache().containsKey(author.getId() + "-" + command)) {
                long remainingCooldown = Cooldown.getRemainingCooldown(author.getId(), command) - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

                if (remainingCooldown > 0) {
                    long cooldownHours = remainingCooldown / 3600;
                    long cooldownMinutes = (remainingCooldown % 3600) / 60;
                    long cooldownSeconds = remainingCooldown % 60;

                    String format = "%1$dh, %2$dm, %3$ds";

                    if (cooldownHours == 0 && cooldownMinutes == 0) {
                        format = "%3$ds";
                    } else if (cooldownSeconds == 0 && cooldownMinutes == 0) {
                        format = "%1$dh";
                    } else if (cooldownHours == 0) {
                        format = "%2$dm, %3ds";
                    } else if (cooldownSeconds == 0) {
                        format = "%1$dh, %2$dm";
                    }

                    String message = I18n.getMessage(author.getId(), "command.cooldown").formatted(":stopwatch:", format);

                    channel.sendMessageFormat(message.formatted(cooldownHours, cooldownMinutes, cooldownSeconds)).queue();

                    return;
                } else {
                    Cooldown.removeCooldown(author.getId(), command);

                    execute(event, commandArgs(event.getMessage()));
                    Cooldown.setCooldown(author.getId(), getCommandName(), this.cooldown());
                }
            } else {
                execute(event, commandArgs(event.getMessage()));
                Cooldown.setCooldown(author.getId(), getCommandName(), this.cooldown());
            }
        }

        postCommandEvent();

        timesCommandUsed++;
    }

    public void postCommandEvent() {}

    protected boolean containsCommand(Message message, MessageReceivedEvent event) {
        String defaultPrefix = RenaConfig.getPrefix();
        String prefix;

        try {
            prefix = DBReadWrite.getValueString(Table.GUILDS, event.getGuild().getId(), "Prefix");

            if (prefix == null) {
                prefix = defaultPrefix;
            }
        } catch (Exception | Error e) {
            prefix = defaultPrefix;
        }

        return (prefix + getCommandName()).equalsIgnoreCase(commandArgs(message)[0]);
    }

    protected String[] commandArgs(Message message) {
        return commandArgs(message.getContentDisplay());
    }

    protected String[] commandArgs(String string) {
        return string.split("\\s+");
    }

    protected Message sendMessage(MessageReceivedEvent event, Message message) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            return event.getPrivateChannel().sendMessage(message).complete();
        } else {
            return event.getTextChannel().sendMessage(message).complete();
        }
    }

    protected Message sendMessage(MessageReceivedEvent event, String message) {
        return sendMessage(event, new MessageBuilder().append(message).build());
    }
}
