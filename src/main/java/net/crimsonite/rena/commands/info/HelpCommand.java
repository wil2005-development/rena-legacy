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

package net.crimsonite.rena.commands.info;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.crimsonite.rena.RenaConfig;
import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.CommandRegistry;
import net.crimsonite.rena.core.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand extends Command {

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        User author = event.getAuthor();
        MessageChannel channel = event.getChannel();

        try {
            Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(roleColor)
                    .setFooter(author.getName(), author.getEffectiveAvatarUrl());

            if (args.length >= 2) {
                Command command = CommandRegistry.getRegisteredCommands().get(args[1]);

                String commandName = command.getCommandName();

                long cooldownTime = command.cooldown();
                long cooldownHours = cooldownTime / 3600;
                long cooldownMinutes = (cooldownTime % 3600) / 60;
                long cooldownSeconds = cooldownTime % 60;

                String timeFormat = "%1$dh, %2$dm, %3$ds";

                if (cooldownHours == 0 && cooldownMinutes == 0) {
                    timeFormat = "%3$ds";
                } else if (cooldownSeconds == 0 && cooldownMinutes == 0) {
                    timeFormat = "%1$dh";
                } else if (cooldownHours == 0) {
                    timeFormat = "%2$dm, %3$ds";
                } else if (cooldownSeconds == 0) {
                    timeFormat = "%1$dh, %2$dm";
                }

                String cooldown = timeFormat.formatted(cooldownHours, cooldownMinutes, cooldownSeconds);

                embed.setTitle(commandName)
                        .addField(I18n.getMessage(author.getId(), "info.help.embed.cooldown"), cooldown, false)
                        .addField(I18n.getMessage(author.getId(), "info.help.embed.usage"), command.getUsage(), false)
                        .addField(I18n.getMessage(author.getId(), "info.help.embed.help_description"), command.getHelp(), false);
            } else {
                List<String> commandCategories = new ArrayList<>();

                for (Command command : CommandRegistry.getRegisteredCommands().values()) {
                    if (!commandCategories.contains(command.getCommandCategory())) {
                        commandCategories.add(command.getCommandCategory());
                    }
                }

                for (String category : commandCategories) {
                    List<String> commandNames = new ArrayList<>();

                    for (Command command : CommandRegistry.getRegisteredCommands().values()) {
                        if (command.getCommandCategory().equals(category)) {
                            commandNames.add(command.getCommandName());
                        }
                    }

                    embed.setTitle(I18n.getMessage(author.getId(), "info.help.embed.title"))
                            .addField(category, commandNames.toString().replace(", ", "`, `").replaceAll("\\[|]", "`"), false);
                }
            }

            channel.sendMessageEmbeds(embed.build()).queue();
        } catch (NullPointerException ignored) {
            channel.sendMessage(I18n.getMessage(author.getId(), "info.help.command_does_not_exist")).queue();
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getGuild() == null) return;

        if (event.getName().equals("help")) {
            event.reply(I18n.getMessage(event.getUser().getId(), "info.help.slash_help").formatted(RenaConfig.getPrefix())).queue();
        }
    }

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String getCommandCategory() {
        return "Information";
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