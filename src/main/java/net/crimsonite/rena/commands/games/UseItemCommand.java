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

package net.crimsonite.rena.commands.games;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.GameHandler;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.crimsonite.rena.entities.Item;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UseItemCommand extends Command {
		
	private void useItem(MessageReceivedEvent event, String itemId, int amount) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		try {			
			Item item = new Item(author.getId(), itemId);
			
			GameHandler.Handler.removeItem(author.getId(), itemId, amount);
			
			switch(item.getAction()) {
				case "RAISE_STATS":
					DBReadWrite.incrementValue(Table.PLAYERS, author.getId(), item.getPrimaryTargetActionField(), item.getPrimaryTargetValue());
					
					channel.sendMessage(I18n.getMessage(author.getId(), "game.use_item.raise_stats".formatted(item.getPrimaryTargetActionField(), item.getPrimaryTargetValue()))).queue();
					
					break;
				default:
					return;
			}
		} catch (IOException e) {
			channel.sendMessage(I18n.getMessage(author.getId(), "common_string.something_went_wrong")).queue();
		}
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
					useItem(event, item, 1);
					
					channel.sendMessage(I18n.getMessage(author.getId(), "game.use_item.success").formatted(1, item)).queue();
				}
				else {
					channel.sendMessage(I18n.getMessage(author.getId(), "game.use_item.lack_item")).queue();
				}
			}
			else {
				channel.sendMessage(I18n.getMessage(author.getId(), "game.use_item.item_not_found")).queue();
			}
		}
		else {
			channel.sendMessage(I18n.getMessage(author.getId(), "game.use_item.no_item_used")).queue();
		}
		
	}

	@Override
	public String getCommandName() {
		return "use";
	}

	@Override
	public String getCommandCategory() {
		return "Games";
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
