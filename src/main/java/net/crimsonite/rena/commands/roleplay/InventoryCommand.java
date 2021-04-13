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

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InventoryCommand extends Command {
	
	private static String replaceItemIdWithName(String str) {
		str = str.replace("ITEM_0X194", "ITEM_OX194")
				.replace("SEED_OF_LIFE", "Seed of Life")
				.replace("SEED_OF_WISDOM", "Seed of Wisdom")
				.replace("ELIXIR_OF_LIFE", "Elixr of Life")
				.replace("ELIXIR_OF_MANA", "Elixr of Mana");
		
		return str;
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
		MessageChannel channel = event.getChannel();
		
		StringBuilder itemField = new StringBuilder();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle("Inventory")
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
		
		Map<String, Long> itemList = DBReadWrite.getValueMapSL(Table.PLAYERS, author.getId(), "INVENTORY");
		Map<String, Integer> inventory = new HashMap<>();
		List<String> itemListKeys = new ArrayList<>(itemList.keySet());
		List<String> inventoryKeys = new ArrayList<>();
		
		for (String item : itemListKeys) {
			if (!(itemList.get(item) == 0)) {
				inventory.put(item, itemList.get(item).intValue());
			}
		}
		
		inventoryKeys.addAll(inventory.keySet());
		
		for (String item : inventoryKeys) {
			int amount = inventory.get(item);
			itemField.append("`%1$s: %2$s`, ".formatted(item, amount));
		}
		
		String currentItems = replaceItemIdWithName(itemField.toString());
		
		embed.addField("Items", currentItems.substring(0, (currentItems.length() - 2)), false);
		
		channel.sendMessage(embed.build()).queue();
	}

	@Override
	public String getCommandName() {
		return "inventory";
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
