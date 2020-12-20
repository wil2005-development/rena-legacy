/*
 * Copyright (C) 2020  Nhalrath
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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

@CommandInfo(
		name = {"shardinfo"},
		description = "Shows the information about the current shard."
		)
public class ShardInfoCmd extends Command{
	
	private static int currentShard;
	private static int totalShard;
	
	public ShardInfoCmd() {
		this.name = "shardinfo";
		this.aliases = new String[] {"shard"};
		this.category = new Category("Informations");
		this.help = "Shows the information about the current shard.";
		this.guildOnly = true;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		currentShard = event.getJDA().getShardInfo().getShardId();
		totalShard = event.getJDA().getShardInfo().getShardTotal();
		
		event.replyFormatted("```" +
				"Current Shard: | %d\n" +
				"Total Shards:   | %d\n" +
				"```",
				currentShard, totalShard
				);
	}

}
