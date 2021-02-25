package net.crimsonite.rena.commands.roleplay;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.database.DBUsers;
import net.crimsonite.rena.engine.RoleplayEngine;
import net.crimsonite.rena.utils.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HuntCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		User author = event.getAuthor();
		
		try {
			//TODO Clean this up, I guess?
			Random rng = new Random();
			Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonData = mapper.readTree(new File("./src/main/resources/rp_assets/enemy.json"));
			String[] enemyList = {"Goblin", "Ogre"};
			String selectedEnemy = enemyList[rng.nextInt(enemyList.length)];
			JsonNode enemyStat = jsonData.get(selectedEnemy);
			JsonNode moneyList = enemyStat.get("MONEY");
			int enemyHP = enemyStat.get("HP").asInt();
			int playerHP = Integer.parseInt(DBUsers.getValueString(author.getId(), "HP"));
			int rewardExp = enemyStat.get("EXP").asInt();
			int rewardMoney = moneyList.get(rng.nextInt(moneyList.size())).asInt();
			EmbedBuilder embedFirst = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle("You encountered a " + selectedEnemy + "!!!")
					.addField("Hp", String.valueOf(enemyHP), true)
					.addField("Mp", enemyStat.get("MP").asText(), true)
					.addField("Atk", enemyStat.get("ATK").asText(), true)
					.addField("Def", enemyStat.get("DEF").asText(), true)
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			channel.sendMessage(embedFirst.build()).queue();
			
			// This loop will keep repeating itself unless either the player or enemy's HP reaches 0 or below
			while (playerHP > 0 && enemyHP > 0) {
				int playerDMG = RoleplayEngine.CommenceBattle.attack(event.getAuthor().getId(), selectedEnemy, "PLAYER");
				enemyHP -= playerDMG;
				
				int enemyDMG = RoleplayEngine.CommenceBattle.attack(event.getAuthor().getId(), selectedEnemy, "PLAYER");
				playerHP -= enemyDMG;
				
				if (enemyHP <= 0) {
					DBUsers.incrementValue(author.getId(), "EXP", rewardExp);
					DBUsers.incrementValue(author.getId(), "MONEY", rewardMoney);
					
					EmbedBuilder embedSecond = new EmbedBuilder()
							.setColor(roleColor)
							.setTitle("You Won!!!")
							.setDescription("You received the following:")
							.addField("Exp", String.valueOf(rewardExp), true)
							.addField("Money", String.valueOf(rewardMoney), true)
							.setFooter(author.getName(), author.getEffectiveAvatarUrl());
					
					channel.sendMessage(embedSecond.build()).queue();
				}
				else if (playerHP <= 0) {					
					EmbedBuilder embedSecond = new EmbedBuilder()
							.setColor(roleColor)
							.setTitle("You Lost!!!")
							.setDescription("The enemy won, and you received nothing.")
							.setFooter(author.getName(), author.getEffectiveAvatarUrl());
					
					channel.sendMessage(embedSecond.build()).queue();
				}
			}
			RoleplayEngine.Handler.handleLevelup(author.getId());
			
		}
		catch (JsonProcessingException ignored) {
			channel.sendMessage("*The monsters suddenly disappeared.*").queue();
		}
		catch (IOException ignored) {
			channel.sendMessage("*Huh? Something's weird is happening...*").queue();
		}
		catch (NullPointerException ignored) {
			DBUsers.registerUser(author.getId());
			channel.sendMessage("Oops! Try again?").queue();
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
