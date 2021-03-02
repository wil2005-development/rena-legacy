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
import java.util.Random;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBUsers;
import net.crimsonite.rena.engine.RoleplayEngine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LootCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		User author = event.getAuthor();
		
		try {
			Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
			Random rng = new Random();
			
			int currentLevel = Integer.parseInt(DBUsers.getValueString(author.getId(), "LEVEL"));
			
			int baseReceivedExp = rng.nextInt(3-1)+1;
			int baseReceivedMoney = rng.nextInt(10-1)+1;
			
			int receivedExp = baseReceivedExp+currentLevel*2;
			int receivedMoney = baseReceivedMoney+currentLevel*2;
			
			DBUsers.incrementValue(author.getId(), "MONEY", receivedMoney);
			DBUsers.incrementValue(author.getId(), "EXP", receivedExp);
			RoleplayEngine.Handler.handleLevelup(author.getId());
			
			EmbedBuilder embed = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle("Looted Goods")
					.addField("Money", String.valueOf(receivedMoney), true)
					.addField("Exp", String.valueOf(receivedExp), true)
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			channel.sendMessage("**You went into an abandoned dungeon and got some loots**").queue();
			channel.sendMessage(embed.build()).queue();
			channel.sendMessage("**Sadly, there wasn't any item of value in there.**").queue();
		}
		catch (NullPointerException ignored) {
			DBUsers.registerUser(author.getId());
			channel.sendMessage("Oops! Try again?").queue();
		}
	}

	@Override
	public String getCommandName() {
		return "loot";
	}

	@Override
	public long cooldown() {
		return 43200;
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

}
