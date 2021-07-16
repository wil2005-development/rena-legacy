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

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.ShardInfo;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

public class ShardInfoCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
		JDA jda = event.getJDA();
		MessageChannel channel = event.getChannel();
		ShardInfo shardInfo = jda.getShardInfo();
		ShardManager shardManager = jda.getShardManager();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(I18n.getMessage(author.getId(), "info.shard_info.embed.title").formatted(jda.getShardInfo().getShardString()))
				.addField(I18n.getMessage(author.getId(), "info.shard_info.embed.shard_id"), "#" + shardInfo.getShardId(), false)
				.addField(I18n.getMessage(author.getId(), "info.shard_info.embed.average_gateway_ping"), "%dm/s".formatted(Math.round(shardManager.getAverageGatewayPing())), true)
				.addBlankField(true)
				.addField(I18n.getMessage(author.getId(), "info.shard_info.embed.total_shards"), String.valueOf(shardManager.getShardsTotal()), true)
				.addField(I18n.getMessage(author.getId(), "info.shard_info.embed.online_shards"), String.valueOf(shardManager.getShardsRunning()), true)
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
		
		channel.sendMessageEmbeds(embed.build()).queue();
		
	}

	@Override
	public String getCommandName() {
		return "shardinfo";
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
