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

import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;
import net.crimsonite.rena.utils.RandomGenerator;

public class RoleplayEngine {
	
	public static class Handler {
		
		private static final int LEVEL_CAP = 50;
				
		private static boolean canIncrementLevel(int level, int exp) {
			int nextLevel = level += 1;
			int requiredExpForNextLevel = 50*nextLevel*(nextLevel+1);
			
			if (exp >= requiredExpForNextLevel && level < LEVEL_CAP) {
				return true;
			}
			
			return false;
		}
		
		/**
		 * Handles the levelup of the player.
		 * 
		 * @param player The Discord UID of the player.
		 */
		public static void handleLevelup(String player) {
			int playerLEVEL = DBReadWrite.getValueInt(Table.PLAYERS, player, "LEVEL");
			int playerEXP = DBReadWrite.getValueInt(Table.PLAYERS, player, "EXP");
			int playerHP = DBReadWrite.getValueInt(Table.PLAYERS, player, "HP");
			int playerMP = DBReadWrite.getValueInt(Table.PLAYERS, player, "MP");
			int playerVIT = DBReadWrite.getValueInt(Table.PLAYERS, player, "VIT");
			int playerWIS = DBReadWrite.getValueInt(Table.PLAYERS, player, "WIS");
			
			boolean canIncrement = canIncrementLevel(playerLEVEL, playerEXP);
			
			if (canIncrement) {
				while (canIncrement) {
					canIncrement = canIncrementLevel(playerLEVEL, playerEXP);
					
					playerLEVEL += 1;
					playerHP += (RandomGenerator.randomInt(((playerVIT/ playerLEVEL) + 1), ((playerVIT / playerLEVEL) + 1) * 2));
					playerMP += (RandomGenerator.randomInt(((playerWIS / playerLEVEL) + 1), ((playerWIS / playerLEVEL) + 1) * 2));
					playerVIT += RandomGenerator.randomInt(1, ((playerVIT / playerLEVEL) + 1));
					playerWIS += RandomGenerator.randomInt(1, ((playerWIS / playerLEVEL) + 1));
				}
				
				DBReadWrite.incrementValue(Table.PLAYERS, player, "LEVEL", playerLEVEL);
				DBReadWrite.incrementValue(Table.PLAYERS, player, "HP", playerHP);
				DBReadWrite.incrementValue(Table.PLAYERS, player, "MP", playerMP);
				DBReadWrite.incrementValue(Table.PLAYERS, player, "VIT", playerVIT);
				DBReadWrite.incrementValue(Table.PLAYERS, player, "WIS", playerWIS);
			}
		}
		
		/**
		 * @param player The Discord UID of the player.
		 * @return Exp required for next level.
		 */
		public static int getRequiredExpForNextLevel(String player) {
			int playerLevel = DBReadWrite.getValueInt(Table.PLAYERS, player, "LEVEL");
			
			int nextLevel = playerLevel += 1;
			int requiredExpForNextLevel = 50*nextLevel*(nextLevel+1);
			
			return requiredExpForNextLevel;
		}
		
		/**
		 * @param player The Dicord UID of the player.
		 * @param amount The amount of Exp to give.
		 */
		public static void giveExp(String player, int amount) {
			int playerLevel = DBReadWrite.getValueInt(Table.PLAYERS, player, "LEVEL");
			
			if (playerLevel < LEVEL_CAP) {
				DBReadWrite.incrementValue(Table.PLAYERS, player, "EXP", amount);
			}
		}
	}

	public static class Battle {
		
		private static int enemyATK;
		private static int enemyDEF;
		private static int playerATK;
		private static int playerDEF;
		
		public enum AttackerType {
			PLAYER,
			ENEMY_NORMAL;
		}
		
		/**
		 * @param enemyDB A place which to look for enemy data.
		 * @param player The Discord UID of the player.
		 * @param enemy The name of the enemy.
		 * @param type The type which is doing the action.
		 * @return damage The damage dealt by the type.
		 * @throws JsonProcessingException
		 * @throws IOException
		 */
		public static int attack(JsonNode enemyDB, String player, String enemy, AttackerType type) throws JsonProcessingException, IOException {
			JsonNode enemyData = enemyDB;
			
			int playerLUK = DBReadWrite.getValueInt(Table.PLAYERS, player, "LUK");
			int playerSTR = DBReadWrite.getValueInt(Table.PLAYERS, player, "STR");
			int defaultCriticalHit = new Random().nextInt(20-1)+1;
			int playerCriticalHit = RandomGenerator.randomInt(1, (playerLUK + 1));
			int damage;
			
			switch (type) {
				case PLAYER:					
					playerATK = DBReadWrite.getValueInt(Table.PLAYERS, player, "ATK");
					enemyDEF = enemyData.get(enemy).get("DEF").asInt();
					
					damage = (int) (2 * Math.pow((playerATK + playerSTR + playerCriticalHit), 2)) / ((playerATK + playerSTR + playerCriticalHit) + enemyDEF);
					
					break;
				case ENEMY_NORMAL:
					enemyATK = enemyData.get(enemy).get("ATK").asInt();
					playerDEF = DBReadWrite.getValueInt(Table.PLAYERS, player, "DEF");
					
					damage = (int) (2 * Math.pow((enemyATK + defaultCriticalHit), 2)) / ((enemyATK + defaultCriticalHit) + playerDEF);
					
					break;
				default:
					playerATK = DBReadWrite.getValueInt(Table.PLAYERS, player, "ATK");
					enemyDEF = enemyData.get(enemy).get("DEF").asInt();
					
					damage = (int) (2 * Math.pow((playerATK + playerSTR + playerCriticalHit), 2)) / ((playerATK + playerSTR + playerCriticalHit) + enemyDEF);
					
					break;	
			}
			
			return damage;
		}
		
	}

}
