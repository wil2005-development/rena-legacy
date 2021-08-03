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

    private final String playerId;
    private final int attack;
    private final int defense;
    private final int level;
    private final int vitality;
    private final int strength;
    private final int agility;
    private final int intelligence;
    private final int wisdom;
    private final int luck;
    private final long health;
    private final long mana;
    private final long experiencePoints;

    /**
     * A Player object that contains the player's attributes.
     *
     * @param playerId Player's Discord ID
     */
    public Player(String playerId) {
        this.playerId = playerId;

        try {
            this.attack = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "ATK");
            this.defense = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "DEF");
            this.level = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "LEVEL");
            this.vitality = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "VIT");
            this.strength = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "STR");
            this.agility = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "AGI");
            this.intelligence = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "INT");
            this.wisdom = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "WIS");
            this.luck = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "LUK");
            this.health = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "HP");
            this.mana = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "MP");
            this.experiencePoints = DBReadWrite.getValueInt(Table.PLAYERS, this.playerId, "EXP");
        } catch (NullPointerException e) {
            throw new NullPointerException("Provided ID is invalid(%s)".formatted(playerId));
        }
    }

    /**
     * @return player's Discord ID as string.
     */
    public String getPlayerId() {
        return this.playerId;
    }

    /**
     * @return player's attack points.
     */
    public int getAttack() {
        return this.attack;
    }

    /**
     * @return player's defense points.
     */
    public int getDefense() {
        return this.defense;
    }

    /**
     * @return player's level.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * @return player's vitality points.
     */
    public int getVitality() {
        return this.vitality;
    }

    /**
     * @return player's strength points.
     */
    public int getStrength() {
        return this.strength;
    }

    /**
     * @return player's agility points.
     */
    public int getAgility() {
        return this.agility;
    }

    /**
     * @return player's intelligence points.
     */
    public int getIntelligence() {
        return this.intelligence;
    }

    /**
     * @return player's wisdom points.
     */
    public int getWisdom() {
        return this.wisdom;
    }

    /**
     * @return player's luck points.
     */
    public int getLuck() {
        return this.luck;
    }

    /**
     * @return player's health.
     */
    public long getHealth() {
        return this.health;
    }

    /**
     * @return player's mana.
     */
    public long getMana() {
        return this.mana;
    }

    /**
     * @return player's experience points.
     */
    public long getExperiencePoints() {
        return this.experiencePoints;
    }

}
