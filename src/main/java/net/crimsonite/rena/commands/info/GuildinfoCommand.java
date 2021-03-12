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
import java.time.format.DateTimeFormatter;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GuildinfoCommand extends Command{

	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		Guild guild = event.getGuild();
		MessageChannel channel = event.getChannel();
		Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(I18n.getMessage("info.guildInfo.field_title") + guild.getName())
				.setThumbnail(guild.getIconUrl())
				.addField(I18n.getMessage("info.guildInfo.field_id"), guild.getId(), false)
				.addField(I18n.getMessage("info.guildInfo.field_dateCreated"), guild.getTimeCreated().format(format), false)
				.addField(I18n.getMessage("info.guildInfo.field_owner"), guild.getOwner().getEffectiveName(), false)
				.addField(I18n.getMessage("info.guildInfo.field_members"), "" + guild.getMemberCount(), true)
				.addField(I18n.getMessage("info.guildInfo.field_roles"), "" + guild.getRoles().size(), true)
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
		
		channel.sendMessage(embed.build()).queue();
	}

	@Override
	public String getCommandName() {
		return "guildinfo";
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
