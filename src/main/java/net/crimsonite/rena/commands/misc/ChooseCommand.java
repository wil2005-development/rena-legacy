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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

@CommandInfo(
		name = {"choose"},
		description = "Chooses between two or more choices."
		)
public class ChooseCommand extends Command{
	
	private static String[] args;
	private static String choice;
	
	public ChooseCommand() {
		this.name = "choose";
		this.aliases = new String[] {"pick"};
		this.category = new Category("Miscellaneous");
		this.help = "Chooses between two or more choices.";
		this.arguments = "[choices]";
		this.guildOnly = true;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		if (event.getAuthor().isBot())
			return;
		
		args = event.getArgs().split(",");
		
		if (event.getArgs().isEmpty()) {
			event.reply("I choose nothing...");
		}
		else {
			choice = args[(int)(Math.random()*args.length)];
			event.replyFormatted("I choose **%s**.", choice);
		}
	}
}
