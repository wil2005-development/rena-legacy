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
import net.crimsonite.rena.core.Cooldown;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.PlayerManager;
import net.crimsonite.rena.core.PlayerManager.Battle.AttackerType;
import net.crimsonite.rena.core.PlayerManager.Handler;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.crimsonite.rena.entities.Player;
import net.crimsonite.rena.utils.RandomGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class HuntCommand extends Command {
	
	private MessageReceivedEvent messageEvent;
	private JsonNode jsonData;
	private Map<String, Map<String, ?>> drops;
	private long timer;
	private StringBuilder battleLog;
	private long dialogueId = 0;
	private long playerId = 0;
	private String selectedEnemy;
	private int enemyHP;
	private int enemyDMG;
	private int playerDMG;
	private int rewardExp;
	private int rewardMoney;
	private boolean locked = true;
	
	private Player player;
	
	private static EmbedBuilder embedForVictory(MessageReceivedEvent event, int rewardExp, int rewardMoney, String rewardItem) {
		User author = event.getAuthor();
		Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(I18n.getMessage(author.getId(), "game.hunt.embed_win.title"))
				.setDescription(I18n.getMessage(author.getId(), "game.hunt.embed_win.description"))
				.addField(I18n.getMessage(author.getId(), "game.hunt.embed_win.exp"), String.valueOf(rewardExp), true)
				.addField(I18n.getMessage(author.getId(), "game.hunt.embed_win.money"), String.valueOf(rewardMoney), true)
				.addField(I18n.getMessage(author.getId(), "game.hunt.embed_win.items"), rewardItem, false)
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
		
		return embed;
	}
	
	private static EmbedBuilder embedForDefeat(MessageReceivedEvent event) {
		User author = event.getAuthor();
		Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(I18n.getMessage(author.getId(), "game.hunt.embed_lost.title"))
				.setDescription(I18n.getMessage(author.getId(), "game.hunt.embed_lost.description"))
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
		
		return embed;
	}
	
	private void throwAttack(MessageReactionAddEvent event, User author) {
		MessageChannel channel = event.getChannel();
		Player player = this.player;
		
		String selectedEnemy = this.selectedEnemy;
		int enemyHP = this.enemyHP;
		int enemyDMG = this.enemyDMG;
		int playerHP = (int) player.getHp();
		int playerDMG = this.playerDMG;
		int rewardExp = this.rewardExp;
		int rewardMoney = this.rewardMoney;
		
		if (event.getMessageIdLong() == this.dialogueId && event.getUserIdLong() == this.playerId) {
			try {
				while (playerHP > 0 && enemyHP > 0) {
					String dialogue = "%1$s attacked and dealt %3$d damage to %2$s\n";
					String status = "%1$s's HP: %3$d | %2$s's HP: %4$d\n\n";
					checkHP(messageEvent, enemyHP, playerHP, rewardExp, rewardMoney, drops);
					
					playerDMG = PlayerManager.Battle.attack(jsonData, author.getId(), selectedEnemy, AttackerType.PLAYER);
					enemyHP -= playerDMG;
					
					if (enemyHP < 0) {
						enemyHP = 0;
					}
					
					battleLog.append(dialogue.formatted(author.getName(), selectedEnemy, playerDMG));
					battleLog.append(status.formatted(author.getName(), selectedEnemy, playerHP, enemyHP));
					
					enemyDMG = PlayerManager.Battle.attack(jsonData, author.getId(), selectedEnemy, AttackerType.ENEMY_NORMAL);
					playerHP -= enemyDMG;
					
					if (playerHP < 0) {
						playerHP = 0;
					}
					
					battleLog.append(dialogue.formatted(selectedEnemy, author.getName(), enemyDMG));
					battleLog.append(status.formatted(author.getName(), selectedEnemy, playerHP, enemyHP));
					
					checkHP(messageEvent, enemyHP, playerHP, rewardExp, rewardMoney, drops);
				}
				PlayerManager.Handler.handleLevelup(author.getId());
				
				channel.sendFile("Battle Logs:\n%sEnd".formatted(battleLog.toString()).getBytes(), "BattleLogs.txt").queue();
			}
			catch(IOException e) {
				channel.sendMessage(I18n.getMessage(author.getId(), "game.hunt.error.io_error")).queue();
			}
		}
	}
	
	private static void checkHP(MessageReceivedEvent event, int enemyHP, int playerHP, int rewardEXP, int rewardMoney, Map<String, Map<String, ?>> drops) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		// Intentionally put here for more entropy.
		Map<String, Integer> itemRewards = new HashMap<String, Integer>();
		
		for (Map<String, ?> item : drops.values()) {
			
			if (RandomGenerator.randomChance((Double) item.get("RATE")))
			{
				itemRewards.put((String) item.get("ID"), (Integer) item.get("AMOUNT"));
			}
		}
		
		List<String> itemRewardsKeySet = new ArrayList<>(itemRewards.keySet());
		
		StringBuilder stringBuilder = new StringBuilder();
		String rewardItem;
		
		for (int i = 0; i < itemRewardsKeySet.size(); i++) {
			stringBuilder.append("%1$s: %2$d, ".formatted(itemRewardsKeySet.get(i), itemRewards.get(itemRewardsKeySet.get(i))));
		}
		
		if (stringBuilder.length() != 0) {
			String temporaryString = stringBuilder.toString();
			
			rewardItem = temporaryString.substring(0, (temporaryString.length() - 2));;
		}
		else {
			stringBuilder.append("None");
			rewardItem = stringBuilder.toString();
		}
		
		if (enemyHP <= 0) {
			Handler.giveExp(author.getId(), rewardEXP);
			DBReadWrite.incrementValue(Table.PLAYERS, author.getId(), "MONEY", rewardMoney);
			
			if (itemRewards != null) {
				for (int i = 0; i < itemRewards.size(); i++) {
					List<String> items = new ArrayList<>(itemRewards.keySet());
					DBReadWrite.incrementValueFromMap(Table.PLAYERS, author.getId(), "INVENTORY", items.get(i), itemRewards.get(items.get(i)));
				}
			}
			
			channel.sendMessage(embedForVictory(event, rewardEXP, rewardMoney, rewardItem).build()).queue();
		}
		else if (playerHP <= 0) {
			
			channel.sendMessage(embedForDefeat(event).build()).queue();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		this.player = new Player(author.getId());
		this.playerId = Long.parseLong(this.player.getPlayerId());
		this.messageEvent = event;
		this.timer = System.currentTimeMillis();
		this.locked = false;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			this.battleLog = new StringBuilder();
			
			Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
			
			this.jsonData = mapper.readTree(getClass().getClassLoader().getResourceAsStream("assets/enemy.json"));
			
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
			
			this.selectedEnemy = enemyList.get(RandomGenerator.randomInt(enemyList.size()));
			
			JsonNode enemyStat = jsonData.get(selectedEnemy);
			JsonNode moneyList = enemyStat.get("MONEY");
			
			this.drops = mapper.convertValue(enemyStat.get("DROPS"), Map.class);
						
			this.enemyHP = enemyStat.get("HP").asInt();
			this.rewardExp = enemyStat.get("EXP").asInt();
			this.rewardMoney = moneyList.get(RandomGenerator.randomInt(moneyList.size())).asInt();
			
			EmbedBuilder embedFirst = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle(I18n.getMessage(event.getAuthor().getId(), "game.hunt.embed_encounter.title").formatted(selectedEnemy))
					.addField(I18n.getMessage(event.getAuthor().getId(), "game.hunt.embed_encounter.hp"), String.valueOf(enemyHP), true)
					.addField(I18n.getMessage(event.getAuthor().getId(), "game.hunt.embed_encounter.mp"), enemyStat.get("MP").asText(), true)
					.addField(I18n.getMessage(event.getAuthor().getId(), "game.hunt.embed_encounter.atk"), enemyStat.get("ATK").asText(), true)
					.addField(I18n.getMessage(event.getAuthor().getId(), "game.hunt.embed_encounter.def"), enemyStat.get("DEF").asText(), true)
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			channel.sendMessage(embedFirst.build()).queue((dialogue)->{
					this.dialogueId = dialogue.getIdLong();
					channel.addReactionById(dialogue.getIdLong(), "\u2705").queue();
					channel.addReactionById(dialogue.getIdLong(), "\u274C").queue();
			});
		}
		catch (JsonProcessingException e) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "game.hunt.error.json_processing_error")).queue();
		}
		catch (IOException e) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "game.hunt.error.io_error")).queue();
		}
		catch (NullPointerException e) {
			DBReadWrite.registerUser(author.getId());
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "game.hunt.error.generic_error")).queue();
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		User author = event.getUser();
		MessageChannel channel = event.getChannel();
		
		boolean huntAccepted = false;
		long currentTime = System.currentTimeMillis();
		long timeout = 60_000;
		
		if(!this.locked) {
			if (event.getMessageIdLong() == this.dialogueId && author.getIdLong() == this.playerId) {
				while (currentTime < (this.timer + timeout)) {
					if (event.getReactionEmote().equals(ReactionEmote.fromUnicode("\u2705", event.getJDA()))) {
						channel.sendMessage(I18n.getMessage(author.getId(), "game.hunt.attack")).queue();
						huntAccepted = true;
						
						break;
					}
					else if (event.getReactionEmote().equals(ReactionEmote.fromUnicode("\u274C", event.getJDA()))) {
						channel.sendMessage(I18n.getMessage(author.getId(), "game.hunt.run")).queue();
						Cooldown.removeCooldown(author.getId(), getCommandName());
						huntAccepted = false;
						
						break;
					}
				}
				this.locked = true;
				
				channel.removeReactionById(this.dialogueId, "\u2705").queue();
				channel.removeReactionById(this.dialogueId, "\u274C").queue();
				
				if (huntAccepted) {
					throwAttack(event, author);
				}
			}
		}
	}

	@Override
	public String getCommandName() {
		return "hunt";
	}
	
	@Override
	public String getCommandCategory() {
		return "Games";
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
