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

package net.crimsonite.rena.core;

import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.crimsonite.rena.entities.Player;
import net.crimsonite.rena.utils.RandomGenerator;

public class GameHandler {

    public static class Handler {

        @SuppressWarnings("unused")
        private static final int EXP_GROWTH_MODIFIER = 50;
        private static final int ITEM_CAP = 250; // Future feature.
        private static final int LEVEL_CAP = 50;

        /**
         * Checks if the player has met the minimum exp requirements to advance to the next level. This only returns a boolean
         * value and does not increment the player's level.
         *
         * @param level Player's level.
         * @param exp   Player's exp.
         * @return True if the player has met the minimum exp requirements in order advance to the next level.
         */
        private static boolean canIncrementLevel(int level, long exp) {
            int nextLevel = level += 1;
            int requiredExpForNextLevel = EXP_GROWTH_MODIFIER * nextLevel * (nextLevel + 1);

            return exp >= requiredExpForNextLevel && level < LEVEL_CAP;
        }

        /**
         * Handles the levelup of the player.
         *
         * @param playerId The Discord UID of the player.
         */
        public static void handleLevelup(String playerId) {
            Player player = new Player(playerId);
            int playerLEVEL = player.getLvl();
            long playerEXP = player.getExp();

            long playerHP = player.getHp();
            long playerMP = player.getMp();

            int playerVIT = player.getVit();
            int playerWIS = player.getWis();

            boolean canIncrementLvl = canIncrementLevel(playerLEVEL, playerEXP);

            if (canIncrementLvl) {
                while (canIncrementLvl) {
                    canIncrementLvl = canIncrementLevel(playerLEVEL, playerEXP);

                    playerLEVEL += 1;

                    playerHP += (RandomGenerator.randomInt(((playerVIT / playerLEVEL) + 1), ((playerVIT / playerLEVEL) + 1) * 2));
                    playerMP += (RandomGenerator.randomInt(((playerWIS / playerLEVEL) + 1), ((playerWIS / playerLEVEL) + 1) * 2));

                    playerVIT += RandomGenerator.randomInt(1, ((playerVIT / playerLEVEL) + 1));
                    playerWIS += RandomGenerator.randomInt(1, ((playerWIS / playerLEVEL) + 1));
                }

                DBReadWrite.incrementValue(Table.PLAYERS, playerId, "LEVEL", playerLEVEL);

                DBReadWrite.incrementValue(Table.PLAYERS, playerId, "HP", (int) playerHP);
                DBReadWrite.incrementValue(Table.PLAYERS, playerId, "MP", (int) playerMP);

                DBReadWrite.incrementValue(Table.PLAYERS, playerId, "VIT", playerVIT);
                DBReadWrite.incrementValue(Table.PLAYERS, playerId, "WIS", playerWIS);
            }
        }

        /**
         * Calculates the total amount of exp required to advance to the next level.
         *
         * @param playerId The Discord UID of the player.
         * @return the total amount of exp required in order to advance to the next level
         */
        public static int getRequiredExpForNextLevel(String playerId) {
            Player player = new Player(playerId);

            int nextLevel = player.getLvl() + 1;
            return EXP_GROWTH_MODIFIER * nextLevel * (nextLevel + 1);
        }

        /**
         * Gives exp to the player.
         *
         * @param playerId The Discord UID of the player.
         * @param amount   Amount of exp to give.
         */
        public static void giveExp(String playerId, int amount) {
            Player player = new Player(playerId);
            int playerLevel = player.getLvl();

            if (playerLevel < LEVEL_CAP) {
                DBReadWrite.incrementValue(Table.PLAYERS, playerId, "EXP", amount);
            }
        }

        /**
         * Gives item to the player.
         *
         * @param playerId The Discord ID of the player.
         * @param itemId   ID of the item to give.
         * @param amount   Amount of item to give.
         */
        public static void giveItem(String playerId, String itemId, int amount) {
            DBReadWrite.incrementValueFromMap(Table.PLAYERS, playerId, "INVENTORY", itemId, amount);
        }

        /**
         * Removes an item from the player.
         *
         * @param playerId The Discord ID of the player.
         * @param itemId   ID of the item to give.
         * @param amount   Amount of item to remove.
         */
        public static void removeItem(String playerId, String itemId, int amount) {
            DBReadWrite.decrementValueFromMap(Table.PLAYERS, playerId, "INVENTORY", itemId, amount);
        }
    }

    public static class Battle {

        public enum AttackerType {
            PLAYER,
            ENEMY_NORMAL,
            ENEMY_BOSS,
            ENEMY_RAID_BOSS,
            ENEMY_EVENT_BOSS,
            ENEMY_WORLD_BOSS
        }

        /**
         * @param enemyDB  A JsonNode on which to look for enemy data.
         * @param playerId The Discord UID of the player.
         * @param enemy    The name of the enemy.
         * @param type     The entity which is doing the action.
         * @return damage The damage dealt by the attacker.
         * @throws JsonProcessingException Something went wrong while parsing the parameter "enemyDB".
         * @throws IOException             Something went wrong while reading "enemyDB".
         */
        public static int attack(JsonNode enemyDB, String playerId, String enemy, AttackerType type) throws JsonProcessingException, IOException {
            Player player = new Player(playerId);

            int playerLUK = player.getLuk();
            int playerSTR = player.getStr();
            int defaultCriticalHit = new Random().nextInt(20 - 1) + 1;
            int playerCriticalHit = RandomGenerator.randomInt(1, (playerLUK + 1));
            int damage;

            switch (type) {
                case PLAYER:
                    int playerATK = player.getAtk();
                    int enemyDEF = enemyDB.get(enemy).get("DEF").asInt();

                    damage = (int) (2 * Math.pow((playerATK + playerSTR + playerCriticalHit), 2)) / ((playerATK + playerSTR + playerCriticalHit) + enemyDEF);

                    break;
                case ENEMY_NORMAL:
                    int enemyATK = enemyDB.get(enemy).get("ATK").asInt();
                    int playerDEF = player.getDef();

                    damage = (int) (2 * Math.pow((enemyATK + defaultCriticalHit), 2)) / ((enemyATK + defaultCriticalHit) + playerDEF);

                    break;
                default:
                    playerATK = player.getAtk();
                    enemyDEF = enemyDB.get(enemy).get("DEF").asInt();

                    damage = (int) (2 * Math.pow((playerATK + playerSTR + playerCriticalHit), 2)) / ((playerATK + playerSTR + playerCriticalHit) + enemyDEF);

                    break;
            }

            return damage;
        }

    }

}
