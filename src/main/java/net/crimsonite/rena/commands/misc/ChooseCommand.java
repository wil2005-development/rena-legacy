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

package net.crimsonite.rena.commands.misc;

import net.crimsonite.rena.utils.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ChooseCommand extends Command{

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		String[] slicedArgs = event.getMessage().getContentRaw().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
		
		if (args.length <= 1) {
			event.getChannel().sendMessage("I choose **nothing**...").queue();
		}
		else {
			String choice = slicedArgs[(int)(Math.random()*slicedArgs.length)];
			event.getChannel().sendMessageFormat("I choose **%s**.", choice).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "choose";
	}
	
	@Override
	public boolean isOwnerCommand() {
		return false;
	}

}
