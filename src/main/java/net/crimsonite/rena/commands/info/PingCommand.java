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

import java.time.temporal.ChronoUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

@CommandInfo(
		name = {"ping"},
		description = "Shows the client and websocket ping."
		)
public class PingCommand extends Command{
	
	private static long ping;
	private static long websocket;
	
	public PingCommand() {
		this.name = "ping";
		this.aliases = new String[] {"latency"};
		this.category = new Category("Informations");
		this.help = "Shows the client and websocket ping.";
		this.guildOnly = false;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		if (event.getAuthor().isBot())
			return;
					
		event.reply("Requesting...", msg -> {
            ping = event.getMessage().getTimeCreated().until(msg.getTimeCreated(), ChronoUnit.MILLIS);
            websocket = event.getJDA().getGatewayPing();
            msg.editMessageFormat("**Ping: **%dms | **Websocket: **%dms", ping, websocket).queue();
        });
	}

}
