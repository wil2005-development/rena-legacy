package net.crimsonite.rena.engine;

import net.crimsonite.rena.database.DBUsers;

public class RoleplayEngine {
	
	public class CommenceBattle {
		
		private String player;
		private String enemy;
		private int playerHP;
		private int enemyHP;
		private int playerMP;
		private int enemyMP;
		private int playerDef;
		private int enemyDef;
		private int playerAttk;
		private int enemyAttk;
		private int playerLevel;
		private int playerExp;
		
		public int attack(String player) {
			playerHP = Integer.parseInt(DBUsers.getValueString(player, "HP"));
			playerMP = Integer.parseInt(DBUsers.getValueString(player, "MP"));
			playerDef = Integer.parseInt(DBUsers.getValueString(player, "Def"));
			playerAttk = Integer.parseInt(DBUsers.getValueString(player, "Attk"));
			
			while (playerHP > 0 || enemyHP > 0) {
				// TODO Add complexity. The code below is just a demonstration on how the value should be returned.
				enemyHP -= playerAttk;
				playerHP -= enemyAttk;
			}
			
			return 0; // The outcome
		}
		
		private boolean checkExp(int level, int exp) {
			int nextLevel = level += 1;
			int requiredExpForNextLevel = 50*nextLevel*(nextLevel+1);
			
			if (exp >= requiredExpForNextLevel) {
				return true;
			}
			
			return false;
		}
		
		public void handleLevelup(String player) {
			playerLevel = Integer.parseInt(DBUsers.getValueString(player, "level"));
			playerExp = Integer.parseInt(DBUsers.getValueString(player, "exp"));
			
			boolean increment = checkExp(playerLevel, playerExp);
			
			if (increment) {
				while (increment) {
					playerLevel += 1;
					increment = checkExp(playerLevel, playerExp);
				}
				
				DBUsers.incrementValue(player, "level", playerLevel);
			}
		}
		
	}

}
