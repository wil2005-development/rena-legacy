package net.crimsonite.rena.engine;

import net.crimsonite.rena.database.DBUsers;

public class RoleplayEngine {
	
	public class commenceBattle {
		String player;
		String enemy;
		int playerHP;
		int enemyHP;
		int playerMP;
		int enemyMP;
		int playerDef;
		int enemyDef;
		int playerAttk;
		int enemyAttk;
		
		public int attack(String playerID) {
			this.player = playerID;
			
			int playerHP = Integer.parseInt(DBUsers.getValueString(player, "HP"));
			int playerMP = Integer.parseInt(DBUsers.getValueString(player, "MP"));
			int playerDef = Integer.parseInt(DBUsers.getValueString(player, "Def"));
			int playerAttk = Integer.parseInt(DBUsers.getValueString(player, "Attk"));
			
			while (playerHP > 0 || enemyHP > 0) {
				// TODO Add complexity. The code below is just a demonstration on how the value should be returned.
				enemyHP -= playerAttk;
				playerHP -= enemyAttk;
			}
			
			return 0; // The outcome
		}
		
	}

}
