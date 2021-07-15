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
import net.crimsonite.rena.core.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RoleinfoCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		if (args.length == 1) {
			EmbedBuilder embed = new EmbedBuilder()
					.setTitle(I18n.getMessage(author.getId(), "info.role_info.embed.title_default").formatted(event.getGuild().getName()))
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			StringBuilder stringBuilder = new StringBuilder();
			for (Role role : event.getGuild().getRoles()) {
				if (!(role.isPublicRole() || role.isManaged())) {
					stringBuilder.append("`%s`, ".formatted(role.getName()));
				}
			}
			
			String roles = stringBuilder.toString();
			
			embed.addField("", roles.substring(0, (roles.length() - 2)), false);
			
			channel.sendMessageEmbeds(embed.build()).queue();
		}
		else if (args.length >= 2) {
			List<Role> listedRoles = FinderUtil.findRoles(args[1], event.getGuild());
			
			if (listedRoles.isEmpty() || listedRoles.get(0).isManaged()) {
				channel.sendMessage(I18n.getMessage(author.getId(), "info.role_info.role_not_found")).queue();
			}
			else {
				Role role = listedRoles.get(0);
				DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
				StringBuilder stringBuilder = new StringBuilder();
				
				for (Permission permission : listedRoles.get(0).getPermissions()) {
					stringBuilder.append("`%s`, ".formatted(permission.getName()));
				}
				
				String permissions = stringBuilder.toString();
				
				EmbedBuilder embed = new EmbedBuilder()
						.setColor(role.getColor())
						.setTitle(I18n.getMessage(author.getId(), "info.role_info.embed.title").formatted(role.getName()))
						.addField(I18n.getMessage(author.getId(), "info.role_info.embed.role_id"), role.getId(), false)
						.addField(I18n.getMessage(author.getId(), "info.role_info.embed.position"), String.valueOf(role.getPosition()), false)
						.addField(I18n.getMessage(author.getId(), "info.role_info.embed.mentionable"), String.valueOf(role.isMentionable()), false)
						.addField(I18n.getMessage(author.getId(), "info.role_info.embed.date_created"), role.getTimeCreated().format(format), false)
						.addField(I18n.getMessage(author.getId(), "info.role_info.embed.role_color"), "#"+Integer.toHexString(role.getColorRaw()).toUpperCase(), false)
						.addField(I18n.getMessage(author.getId(), "info.role_info.embed.guild"), role.getGuild().getName(), false)
						.addField(I18n.getMessage(author.getId(), "info.role_info.embed.permissions"), permissions.substring(0, (permissions.length() - 2)), false)
						.setFooter(author.getName(), author.getEffectiveAvatarUrl());
				
				channel.sendMessageEmbeds(embed.build()).queue();
			}
		}
	}

	@Override
	public String getCommandName() {
		return "roleinfo";
	}
	
	@Override
	public String getCommandCategory() {
		return "Information";
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
