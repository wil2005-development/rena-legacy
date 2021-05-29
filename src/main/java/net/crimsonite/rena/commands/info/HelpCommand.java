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

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.CommandRegistry;
import net.crimsonite.rena.core.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand extends Command {
	
	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		try {
			User author = event.getAuthor();
			Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
			
			EmbedBuilder embed = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle(I18n.getMessage(event.getAuthor().getId(), "info.help.embed.title"))
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			if (args.length >= 2) {
				Command command = CommandRegistry.getRegisteredCommands().get(args[1]);
				
				String commandName = command.getCommandName();
				
				long cooldownTime = command.cooldown();
				long cooldownHours = cooldownTime / 3600;
				long cooldownMinutes = (cooldownTime % 3600) / 60;
				long cooldownSeconds = cooldownTime % 60;
				
				String timeFormat = I18n.getMessage(event.getAuthor().getId(), "info.help.embed.cooldown_duration_HMS");
				
				if (cooldownHours == 0 && cooldownMinutes == 0) {
					timeFormat = I18n.getMessage(event.getAuthor().getId(), "info.help.embed.cooldown_duration_S");
				}
				else if (cooldownSeconds == 0 && cooldownMinutes == 0) {
					timeFormat = I18n.getMessage(event.getAuthor().getId(), "info.help.embed.cooldown_duration_H");
				}
				else if (cooldownHours == 0) {
					timeFormat = I18n.getMessage(event.getAuthor().getId(), "info.help.embed.cooldown_duration_MS");
				}
				else if (cooldownSeconds == 0) {
					timeFormat = I18n.getMessage(event.getAuthor().getId(), "info.help.embed.cooldown_duration_HM");
				}
				
				String description = timeFormat.formatted(cooldownHours, cooldownMinutes, cooldownSeconds);
				
				embed.addField(commandName, description, false);
			}
			else {
				List<String> commandCategory = new ArrayList<>();
				
				for (Command command : CommandRegistry.getRegisteredCommands().values()) {
					if (!commandCategory.contains(command.getCommandCategory())) {
						commandCategory.add(command.getCommandCategory());
					}
				}
				
				for (int i = 0; i < commandCategory.size(); i++) {
					List<String> currentBatchOfCommands = new ArrayList<>();
					
					for (Command command : CommandRegistry.getRegisteredCommands().values()) {
						if (command.getCommandCategory() == commandCategory.get(i)) {
							currentBatchOfCommands.add(command.getCommandName());
						}
					}
					
					embed.addField(commandCategory.get(i), currentBatchOfCommands.toString().replace(", ", "`, `").replaceAll("\\[|]", "`"), false);
				}
			}
			
			channel.sendMessage(embed.build()).queue();
		}
		catch (NullPointerException ignored) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "info.help.command_does_not_exist")).queue();
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

}