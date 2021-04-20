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

package net.crimsonite.rena;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(ReadyListener.class);
	
	@Override
	public void onReady(ReadyEvent event) {
		int shardId = event.getJDA().getShardInfo().getShardId();
		
		event.getJDA().getPresence().setActivity(Activity.watching("over you at shard #%d".formatted(shardId)));
		
		logger.info("Shard #%1$d activated in %2$d second(s).".formatted(shardId, ((System.currentTimeMillis()-RenaBot.startup)/1000)));
	}
	
}
