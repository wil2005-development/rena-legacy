package net.crimsonite.rena.engine;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.database.DBUsers;

public class RoleplayEngine {
	
	public static class Handler {
		
		private static int playerLevel;
		private static int playerExp;
		
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
			playerLevel = Integer.parseInt(DBUsers.getValueString(player, "LEVEL"));
			playerExp = Integer.parseInt(DBUsers.getValueString(player, "EXP"));
			
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
	// TODO move the exp handling outside of this class
	public static class CommenceBattle {
		
		private static String player;
		private static String enemy;
		private static int playerHP;
		private static int enemyHP;
		private static int playerMP;
		private static int enemyMP;
		private static int playerDef;
		private static int enemyDef;
		private static int playerATK;
		private static int enemyATK;
		private static int playerLevel;
		private static int playerExp;
		
		/**
		 * @param player -The Discord UID of the player.
		 * @param enemy -The name of the enemy.
		 * @return damage -The damage dealt by the player.
		 * @throws JsonProcessingException
		 * @throws IOException
		 */
		public static int attack(String player, String enemy) throws JsonProcessingException, IOException {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode enemyData = mapper.readTree(new File("./src/main/resources/rp_assets/enemy.json"));
			
			playerATK = Integer.parseInt(DBUsers.getValueString(player, "ATK"));
			int enemyDef = enemyData.get(enemy).get("DEF").asInt();
			int damage = playerATK*((25+enemyDef)/25);

			return damage;
		}
	}

}
