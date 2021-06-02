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

package net.crimsonite.rena.entities;

import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;

public class Player {
	
	private String aPlayerId;
	private int aAtk;
	private int aDef;
	private int aLvl;
	private int aVit;
	private int aStr;
	private int aAgi;
	private int aInt;
	private int aWis;
	private int aLuk;
	private long aHp;
	private long aMp;
	private long aExp;
	
	/**
	 * A Player object that contains the player's attributes.
	 * 
	 * @param playerId Player's Discord ID
	 */
	public Player(String playerId) {
		this.aPlayerId = playerId;
		
		try {
			this.aAtk = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "ATK");
			this.aDef = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "DEF");
			this.aLvl = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "LVL");
			this.aVit = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "VIT");
			this.aStr = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "STR");
			this.aAgi = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "AGI");
			this.aInt = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "INT");
			this.aWis = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "WIS");
			this.aLuk = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "LUK");
			this.aHp = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "HP");
			this.aMp = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "MP");
			this.aExp = DBReadWrite.getValueInt(Table.PLAYERS, playerId, "EXP");
		}
		catch (NullPointerException e) {
			throw new NullPointerException("Provided ID is invalid($s)".formatted(playerId));
		}
	}
	
	/**
	 * @return player's Discord ID in String format.
	 */
	public String getPlayerId() {
		return this.aPlayerId;
	}
	
	/**
	 * @return player's attack attribute.
	 */
	public int getAtk() {
		return this.aAtk;
	}
	
	/**
	 * @return player's defense attribute.
	 */
	public int getDef() {
		return this.aDef;
	}
	
	/**
	 * @return player's level.
	 */
	public int getLvl() {
		return this.aLvl;
	}
	
	/**
	 * @return player's vitality attribute.
	 */
	public int getVit() {
		return this.aVit;
	}
	
	/**
	 * @return player's strength attribute.
	 */
	public int getStr() {
		return this.aStr;
	}
	
	/**
	 * @return player's agility attribute.
	 */
	public int getAgi() {
		return this.aAgi;
	}
	
	/**
	 * @return player's intelligence attribute.
	 */
	public int getInt() {
		return this.aInt;
	}
	
	/**
	 * @return player's wisdom attribute.
	 */
	public int getWis() {
		return this.aWis;
	}
	
	/**
	 * @return player's luck attribute.
	 */
	public int getLuk() {
		return this.aLuk;
	}
	
	/**
	 * @return player's HP attribute.
	 */
	public long getHp() {
		return this.aHp;
	}
	
	/**
	 * @return player's MP attribute.
	 */
	public long getMp() {
		return this.aMp;
	}
	
	/**
	 * @return player's experience points.
	 */
	public long getExp() {
		return this.aExp;
	}

}
