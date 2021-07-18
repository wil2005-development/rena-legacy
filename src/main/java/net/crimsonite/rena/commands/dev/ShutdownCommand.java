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

package net.crimsonite.rena.commands.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.crimsonite.rena.RenaConfig;
import net.crimsonite.rena.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

public class ShutdownCommand extends Command {
	
	private static final Logger logger = LoggerFactory.getLogger(ShutdownCommand.class);

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		JDA jda = event.getJDA();
		ShardManager shardManager = jda.getShardManager();
		
		logger.info("Shutting down...");
		
		if (RenaConfig.isSharding()) {
			long shardCount = RenaConfig.getTotalShards();
			
			for (int i = 0; i < shardCount; i++) {
				logger.info("Shutting down shard %d", i);
				
				shardManager.shutdown(i);
			}
		}
		
		else {
			jda.shutdown();
		}
		
		System.exit(0);
	}
	
	@Override
	public String getCommandName() {
		return "shutdown";
	}
	
	@Override
	public String getCommandCategory() {
		return "Dev";
	}

	@Override
	public boolean isOwnerCommand() {
		return true;
	}

	@Override
	public long cooldown() {
		return 0;
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return null;
	}

}
