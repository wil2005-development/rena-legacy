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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class ShardInfoCommand extends Command{
	
	private static User author;
	private static Color roleColor;
	private static int currentShard;
	private static int totalShard;
	
	public ShardInfoCommand() {
		this.name = "shardinfo";
		this.aliases = new String[] {"shard"};
		this.category = new Category("Informations");
		this.help = "Shows the information about the current shard.";
		this.guildOnly = true;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		author = event.getAuthor();
		roleColor = event.getGuild().retrieveMember(author).complete().getColor();
		currentShard = event.getJDA().getShardInfo().getShardId();
		totalShard = event.getJDA().getShardInfo().getShardTotal();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle("Shard Info")
				.setThumbnail(event.getSelfUser().getEffectiveAvatarUrl())
				.addField("Current Shard", ""+currentShard, true)
				.addField("Total Shard", ""+totalShard, true)
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
		
		event.reply(embed.build());
	}

}
