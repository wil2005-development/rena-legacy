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
import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;
import net.crimsonite.rena.engine.I18n;
import net.crimsonite.rena.engine.RoleplayEngine;
import net.crimsonite.rena.engine.RoleplayEngine.CommenceBattle.AttackerType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HuntCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			Random rng = new Random();
			
			Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
			
			JsonNode jsonData = mapper.readTree(getClass().getClassLoader().getResourceAsStream("rp_assets/enemy.json"));
			
			String[] enemyList = {"Goblin", "Ogre"};
			String selectedEnemy = enemyList[rng.nextInt(enemyList.length)];
			
			JsonNode enemyStat = jsonData.get(selectedEnemy);
			JsonNode moneyList = enemyStat.get("MONEY");
			
			int enemyHP = enemyStat.get("HP").asInt();
			int playerHP = DBReadWrite.getValueInt(Table.USERS, author.getId(), "HP");
			int rewardExp = enemyStat.get("EXP").asInt();
			int rewardMoney = moneyList.get(rng.nextInt(moneyList.size())).asInt();
			
			EmbedBuilder embedFirst = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle(I18n.getMessage("roleplay.hunt.embed_encounter.title").formatted(selectedEnemy))
					.addField(I18n.getMessage("roleplay.hunt.embed_encounter.hp"), String.valueOf(enemyHP), true)
					.addField(I18n.getMessage("roleplay.hunt.embed_encounter.mp"), enemyStat.get("MP").asText(), true)
					.addField(I18n.getMessage("roleplay.hunt.embed_encounter.atk"), enemyStat.get("ATK").asText(), true)
					.addField(I18n.getMessage("roleplay.hunt.embed_encounter.def"), enemyStat.get("DEF").asText(), true)
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			channel.sendMessage(embedFirst.build()).queue();
			
			// This loop will keep repeating itself unless either the player or enemy's HP reaches 0 or below
			while (playerHP > 0 && enemyHP > 0) {
				int playerDMG = RoleplayEngine.CommenceBattle.attack(jsonData, event.getAuthor().getId(), selectedEnemy, AttackerType.PLAYER);
				enemyHP -= playerDMG;
				
				int enemyDMG = RoleplayEngine.CommenceBattle.attack(jsonData, event.getAuthor().getId(), selectedEnemy, AttackerType.ENEMY_NORMAL);
				playerHP -= enemyDMG;
				
				if (enemyHP <= 0) {
					DBReadWrite.incrementValue(Table.USERS, author.getId(), "EXP", rewardExp);
					DBReadWrite.incrementValue(Table.USERS, author.getId(), "MONEY", rewardMoney);
					
					EmbedBuilder embedSecond = new EmbedBuilder()
							.setColor(roleColor)
							.setTitle(I18n.getMessage("roleplay.hunt.embed_win.title"))
							.setDescription(I18n.getMessage("roleplay.hunt.embed_win.description"))
							.addField(I18n.getMessage("roleplay.hunt.embed_win.exp"), String.valueOf(rewardExp), true)
							.addField(I18n.getMessage("roleplay.hunt.embed_win.money"), String.valueOf(rewardMoney), true)
							.setFooter(author.getName(), author.getEffectiveAvatarUrl());
					
					channel.sendMessage(embedSecond.build()).queue();
				}
				else if (playerHP <= 0) {					
					EmbedBuilder embedSecond = new EmbedBuilder()
							.setColor(roleColor)
							.setTitle(I18n.getMessage("roleplay.hunt.embed_lost.title"))
							.setDescription(I18n.getMessage("roleplay.hunt.embed_lost.description"))
							.setFooter(author.getName(), author.getEffectiveAvatarUrl());
					
					channel.sendMessage(embedSecond.build()).queue();
				}
			}
			RoleplayEngine.Handler.handleLevelup(author.getId());
			
		}
		catch (JsonProcessingException ignored) {
			channel.sendMessage(I18n.getMessage("roleplay.hunt.error.json_processing_error")).queue();
		}
		catch (IOException ignored) {
			channel.sendMessage(I18n.getMessage("roleplay.hunt.error.io_error")).queue();
		}
		catch (NullPointerException ignored) {
			DBReadWrite.registerUser(author.getId());
			channel.sendMessage(I18n.getMessage("roleplay.hunt.error.generic_error")).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "hunt";
	}

	@Override
	public long cooldown() {
		return 28800;
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

}
