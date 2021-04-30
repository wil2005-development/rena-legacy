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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;
import net.crimsonite.rena.engine.I18n;
import net.crimsonite.rena.engine.RoleplayEngine;
import net.crimsonite.rena.engine.RoleplayEngine.Battle.AttackerType;
import net.crimsonite.rena.engine.RoleplayEngine.Handler;
import net.crimsonite.rena.utils.RandomGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HuntCommand extends Command {
	
	private static void checkHP(User author, MessageChannel channel, EmbedBuilder embedForVictory, EmbedBuilder embedForDefeat, int enemyHP, int playerHP, int rewardEXP, int rewardMoney) {
		if (enemyHP <= 0) {
			Handler.giveExp(author.getId(), rewardEXP);
			DBReadWrite.incrementValue(Table.PLAYERS, author.getId(), "MONEY", rewardMoney);
			
			channel.sendMessage(embedForVictory.build()).queue();
		}
		else if (playerHP <= 0) {
			
			channel.sendMessage(embedForDefeat.build()).queue();
		}
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			StringBuilder battleLog = new StringBuilder();
			
			Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
			
			JsonNode jsonData = mapper.readTree(getClass().getClassLoader().getResourceAsStream("assets/enemy.json"));
			
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("assets/enemy_list.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			List<String> enemyList = new ArrayList<>();
			
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					enemyList.add(line);
				}
			}
			catch (IOException e) {}
			
			String selectedEnemy = enemyList.get(RandomGenerator.randomInt(enemyList.size()));
			
			JsonNode enemyStat = jsonData.get(selectedEnemy);
			JsonNode moneyList = enemyStat.get("MONEY");
			
			@SuppressWarnings("unchecked")
			Map<String, Map<String, ?>> drops = mapper.convertValue(enemyStat.get("DROPS"), Map.class);
			Map<String, Integer> itemRewards = new HashMap<String, Integer>();
			
			for (Map<String, ?> item : drops.values()) {
				
				if (RandomGenerator.randomChance((Double) item.get("RATE")))
				{
					itemRewards.put((String) item.get("ID"), (Integer) item.get("AMOUNT"));
				}
			}
			
			System.out.print(itemRewards.toString());
						
			int enemyHP = enemyStat.get("HP").asInt();
			int enemyDMG;
			int playerHP = DBReadWrite.getValueInt(Table.PLAYERS, author.getId(), "HP");
			int playerDMG;
			int rewardExp = enemyStat.get("EXP").asInt();
			int rewardMoney = moneyList.get(RandomGenerator.randomInt(moneyList.size())).asInt();
			
			EmbedBuilder embedFirst = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_encounter.title").formatted(selectedEnemy))
					.addField(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_encounter.hp"), String.valueOf(enemyHP), true)
					.addField(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_encounter.mp"), enemyStat.get("MP").asText(), true)
					.addField(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_encounter.atk"), enemyStat.get("ATK").asText(), true)
					.addField(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_encounter.def"), enemyStat.get("DEF").asText(), true)
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			EmbedBuilder embedForVictory = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_win.title"))
					.setDescription(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_win.description"))
					.addField(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_win.exp"), String.valueOf(rewardExp), true)
					.addField(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_win.money"), String.valueOf(rewardMoney), true)
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			EmbedBuilder embedForDefeat = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_lost.title"))
					.setDescription(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.embed_lost.description"))
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			channel.sendMessage(embedFirst.build()).queue();
			
			while (playerHP > 0 && enemyHP > 0) {
				String dialogue = "%1$s attacked and dealt %3$d damage to %2$s\n";
				String status = "%1$s's HP: %3$d | %2$s's HP: %4$d\n\n";
				checkHP(author, channel, embedForVictory, embedForDefeat, enemyHP, playerHP, rewardExp, rewardMoney);
				
				playerDMG = RoleplayEngine.Battle.attack(jsonData, event.getAuthor().getId(), selectedEnemy, AttackerType.PLAYER);
				enemyHP -= playerDMG;
				
				if (enemyHP < 0) {
					enemyHP = 0;
				}
				
				battleLog.append(dialogue.formatted(author.getName(), selectedEnemy, playerDMG));
				battleLog.append(status.formatted(author.getName(), selectedEnemy, playerHP, enemyHP));
				
				enemyDMG = RoleplayEngine.Battle.attack(jsonData, event.getAuthor().getId(), selectedEnemy, AttackerType.ENEMY_NORMAL);
				playerHP -= enemyDMG;
				
				if (playerHP < 0) {
					playerHP = 0;
				}
				
				battleLog.append(dialogue.formatted(selectedEnemy, author.getName(), enemyDMG));
				battleLog.append(status.formatted(author.getName(), selectedEnemy, playerHP, enemyHP));
				
				checkHP(author, channel, embedForVictory, embedForDefeat, enemyHP, playerHP, rewardExp, rewardMoney);
			}
			RoleplayEngine.Handler.handleLevelup(author.getId());
			
			channel.sendFile("Battle Logs:\n%sEnd".formatted(battleLog.toString()).getBytes(), "BattleLogs.txt").queue();
		}
		catch (JsonProcessingException ignored) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.error.json_processing_error")).queue();
		}
		catch (IOException ignored) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.error.io_error")).queue();
		}
		catch (NullPointerException ignored) {
			DBReadWrite.registerUser(author.getId());
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "roleplay.hunt.error.generic_error")).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "hunt";
	}
	
	@Override
	public String getCommandCategory() {
		return "Roleplay";
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
