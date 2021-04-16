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

public class RoleplayEngine {
	
	public static class Handler {
		
		private static final int LEVEL_CAP = 50;
				
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
			int playerLevel = DBReadWrite.getValueInt(Table.PLAYERS, player, "LEVEL");
			int playerExp = DBReadWrite.getValueInt(Table.PLAYERS, player, "EXP");
			int playerHP = DBReadWrite.getValueInt(Table.PLAYERS, player, "HP");
			int playerMP = DBReadWrite.getValueInt(Table.PLAYERS, player, "MP");
			
			boolean canIncrement = checkExp(playerLevel, playerExp);
			
			if (canIncrement) {
				while (canIncrement && playerLevel <= LEVEL_CAP) {
					playerLevel += 1;
					canIncrement = checkExp(playerLevel, playerExp);
				}
				
				DBReadWrite.incrementValue(Table.PLAYERS, player, "LEVEL", playerLevel);
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
		 * @param enemyDB -A place which to look for enemy data.
		 * @param player -The Discord UID of the player.
		 * @param enemy -The name of the enemy.
		 * @param type -The type which is doing the action.
		 * @return damage -The damage dealt by the type.
		 * @throws JsonProcessingException
		 * @throws IOException
		 */
		public static int attack(JsonNode enemyDB, String player, String enemy, AttackerType type) throws JsonProcessingException, IOException {
			JsonNode enemyData = enemyDB;
			
			int criticalHIT = new Random().nextInt(20-1)+1;
			int damage = 0;
			
			switch (type) {
				case PLAYER:					
					playerATK = DBReadWrite.getValueInt(Table.PLAYERS, player, "ATK");
					enemyDEF = enemyData.get(enemy).get("DEF").asInt();
					
					damage = (playerATK+criticalHIT)*((25+enemyDEF)/25);
					
					break;
				case ENEMY_NORMAL:
					enemyATK = enemyData.get(enemy).get("ATK").asInt();
					playerDEF = DBReadWrite.getValueInt(Table.PLAYERS, player, "DEF");
					
					damage = (enemyATK+criticalHIT)*((25+playerDEF)/25);
					
					break;
				default:
					playerATK = DBReadWrite.getValueInt(Table.PLAYERS, player, "ATK");
					enemyDEF = enemyData.get(enemy).get("DEF").asInt();
					
					damage = (playerATK+criticalHIT)*((25+enemyDEF)/25);
					
					break;	
			}
			
			return damage;
		}
		
	}

}
