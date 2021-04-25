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

package net.crimsonite.rena.commands.roleplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UseItemCommand extends Command {
	
	private static void useItem(User author, String item, int amount) {
		DBReadWrite.decrementValueFromMap(Table.PLAYERS, author.getId(), "INVENTORY", item, 1);
		
		// TODO do something.
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		if (args.length >= 2) {
			String item = args[1].toUpperCase();
			Map<String, Long> inventory = DBReadWrite.getValueMapSL(Table.PLAYERS, author.getId(), "INVENTORY");
			List<String> allItems = new ArrayList<>(inventory.keySet());
			
			if (allItems.contains(item)) {
				if (inventory.get(item) >= 1) {
					useItem(author, item, 1);
					
					// TODO Success message.
				}
				else {
					channel.sendMessage(I18n.getMessage(author.getId(), "roleplay.use_item.lack_item")).queue();
				}
			}
			else {
				channel.sendMessage(I18n.getMessage(author.getId(), "roleplay.use_item.item_not_found")).queue();
			}
		}
		else {
			channel.sendMessage(I18n.getMessage(author.getId(), "roleplay.use_item.no_item_used")).queue();
		}
		
	}

	@Override
	public String getCommandName() {
		return "use";
	}

	@Override
	public String getCommandCategory() {
		return "Roleplay";
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
