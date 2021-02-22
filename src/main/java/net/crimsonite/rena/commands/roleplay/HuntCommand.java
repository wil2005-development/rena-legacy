package net.crimsonite.rena.commands.roleplay;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.engine.RoleplayEngine;
import net.crimsonite.rena.utils.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HuntCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		try {
			Random rng = new Random();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonData = mapper.readTree(new File("./src/main/resources/rp_assets/enemy.json"));
			String[] enemyList = {"Goblin", "Ogre"};
			String selectedEnemy = enemyList[rng.nextInt(enemyList.length-1)+1];
			JsonNode enemyStat = jsonData.get(selectedEnemy);
			int enemyHP = enemyStat.get("HP").asInt();
			int playerHP = 15;
			
			while (playerHP > 0 && enemyHP > 0) {
				int dmg = RoleplayEngine.CommenceBattle.attack(event.getAuthor().getId(), selectedEnemy);
				enemyHP -= dmg;
				channel.sendMessageFormat("Dmg: %d, EnemyHP: %d", dmg, enemyHP).queue();
			}
			
		}
		catch (JsonProcessingException ignored) {
			channel.sendMessage("*The monsters suddenly disappeared.*").queue();
		}
		catch (IOException ignored) {
			channel.sendMessage("*Huh? Something's weird is happening...*").queue();
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
