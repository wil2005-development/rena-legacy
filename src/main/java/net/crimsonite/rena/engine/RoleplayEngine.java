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

package net.crimsonite.rena.engine;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.database.DBUsers;

public class RoleplayEngine {
	
	public static class Handler {
				
		private static boolean checkExp(int level, int exp) {
			int nextLevel = level += 1;
			int requiredExpForNextLevel = 50*nextLevel*(nextLevel+1);
			
			if (exp >= requiredExpForNextLevel) {
				return true;
			}
			
			return false;
		}
		
		/**
		 * Handles the levelup of the player.
		 * 
		 * @param -The Discord UID of the player.
		 */
		public static void handleLevelup(String player) {
			int playerLevel = Integer.parseInt(DBUsers.getValueString(player, "LEVEL"));
			int playerExp = Integer.parseInt(DBUsers.getValueString(player, "EXP"));
			
			boolean increment = checkExp(playerLevel, playerExp);
			
			if (increment) {
				while (increment) {
					playerLevel += 1;
					increment = checkExp(playerLevel, playerExp);
				}
				
				DBUsers.incrementValue(player, "LEVEL", playerLevel);
			}
		}
	}

	public static class CommenceBattle {
		
		private static int enemyATK;
		private static int enemyDEF;
		private static int playerATK;
		private static int playerDEF;
		
		/**
		 * @param player -The Discord UID of the player.
		 * @param enemy -The name of the enemy.
		 * @param type -The type which is doing the action.
		 * @return damage -The damage dealt by the type.
		 * @throws JsonProcessingException
		 * @throws IOException
		 */
		public static int attack(String player, String enemy, String type) throws JsonProcessingException, IOException {
			ObjectMapper mapper;
			JsonNode enemyData;
			
			int criticalHIT = new Random().nextInt(20-1)+1;
			int damage = 0;
			
			switch (type) {
				case "PLAYER":
					mapper = new ObjectMapper();
					enemyData = mapper.readTree(new File("src/main/resources/rp_assets/enemy.json"));
					
					playerATK = Integer.parseInt(DBUsers.getValueString(player, "ATK"));
					enemyDEF = enemyData.get(enemy).get("DEF").asInt();
					damage = (playerATK+criticalHIT)*((25+enemyDEF)/25);
					
					break;
				case "ENEMY":
					mapper = new ObjectMapper();
					enemyData = mapper.readTree(new File("src/main/resources/rp_assets/enemy.json"));
					
					enemyATK = enemyData.get(enemy).get("ATK").asInt();
					playerDEF = Integer.parseInt(DBUsers.getValueString(player, "DEF"));
					damage = (enemyATK+criticalHIT)*((25+playerDEF)/25);
					
					break;
				default:
					mapper = new ObjectMapper();
					enemyData = mapper.readTree(new File("src/main/resources/rp_assets/enemy.json"));
					
					playerATK = Integer.parseInt(DBUsers.getValueString(player, "ATK"));
					enemyDEF = enemyData.get(enemy).get("DEF").asInt();
					damage = (playerATK+criticalHIT)*((25+enemyDEF)/25);
					
					break;	
			}
			
			return damage;
		}
		
	}

}
