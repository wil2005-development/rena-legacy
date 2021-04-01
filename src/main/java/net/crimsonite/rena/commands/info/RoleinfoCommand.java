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

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RoleinfoCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		if (args.length == 1) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "info.role_info.no_role_provided")).queue();
		}
		else if (args.length >= 2) {
			List<Role> listedRoles = FinderUtil.findRoles(args[1], event.getGuild());
			
			if (listedRoles.isEmpty()) {
				channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "info.role_info.role_not_found")).queue();
			}
			else {
				Role role = listedRoles.get(0);
				User author = event.getAuthor();
				DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
				
				EmbedBuilder embed = new EmbedBuilder()
						.setColor(role.getColor())
						.setTitle(I18n.getMessage(event.getAuthor().getId(), "info.role_info.embed.title").formatted(role.getName()))
						.addField(I18n.getMessage(event.getAuthor().getId(), "info.role_info.embed.role_id"), role.getId(), false)
						.addField(I18n.getMessage(event.getAuthor().getId(), "info.role_info.embed.position"), String.valueOf(role.getPosition()), false)
						.addField(I18n.getMessage(event.getAuthor().getId(), "info.role_info.embed.mentionable"), String.valueOf(role.isMentionable()), false)
						.addField(I18n.getMessage(event.getAuthor().getId(), "info.role_info.embed.date_created"), role.getTimeCreated().format(format), false)
						.addField(I18n.getMessage(event.getAuthor().getId(), "info.role_info.embed.role_color"), "#"+Integer.toHexString(role.getColorRaw()).toUpperCase(), false)
						.addField(I18n.getMessage(event.getAuthor().getId(), "info.role_info.embed.guild"), role.getGuild().getName(), false)
						.addField(I18n.getMessage(event.getAuthor().getId(), "info.role_info.embed.permissions"), role.getPermissions().toString(), false)
						.setFooter(author.getName(), author.getEffectiveAvatarUrl());
				
				channel.sendMessage(embed.build()).queue();
			}
		}
	}

	@Override
	public String getCommandName() {
		return "roleinfo";
	}

	@Override
	public long cooldown() {
		return 5;
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

}
