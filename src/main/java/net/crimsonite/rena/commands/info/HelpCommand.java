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
import java.util.HashMap;

import net.crimsonite.rena.RenaBot;
import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand extends Command {
	
	private HashMap<String, Command> commands;
	private static int numberOfCommands = 0;
		
	public HelpCommand() {
		commands = new HashMap<>();
	}
	
	/**
	 * @return the number of registered commands
	 */
	public static int getCommandCount() {
		return numberOfCommands;
	}
	
	/**
	 * Registers and returns the command passed
	 * 
	 * @param command
	 * @return the command passed
	 */
	public Command registerCommand(Command command) {
		commands.put(command.getCommandName(), command);
		numberOfCommands++;
		
		return command;
	}
	
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
				Command command = RenaBot.commandRegistry.commands.get(args[1]);
				
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
				for (Command command : RenaBot.commandRegistry.commands.values()) {
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
	public boolean isOwnerCommand() {
		return false;
	}

	@Override
	public long cooldown() {
		return 5;
	}

}