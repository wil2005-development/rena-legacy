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

import net.crimsonite.rena.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShutdownCommand extends Command {
	
	private static final Logger logger = LoggerFactory.getLogger(ShutdownCommand.class);

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		JDA jda = event.getJDA();
		
		logger.info("Shutting down...");
		
		jda.shutdown();
		System.exit(0); // Maybe remove this?
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

}
