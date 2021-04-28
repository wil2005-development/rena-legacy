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

package net.crimsonite.rena.utils;

import java.util.Random;

public class RandomGenerator {
	
	private static Random defaultRNG = new Random();
	
	/**
	 * @param min Minimum number to generate.
	 * @param max Maximum number to generate.
	 * @return Randomly generated number.
	 */
	public static int randomInt(int min, int max) {
		return defaultRNG.nextInt(max - min) + min;
	}
	
	/**
	 * @param max Maximum number to generate.
	 * @return Randomly generated number.
	 */
	public static float randomFloat(float max) {
		return defaultRNG.nextFloat() * max;
	}
	
	/**
	 * @param min Minimum number to generate.
	 * @param max Maximum number to generate.
	 * @return Randomly generated number.
	 */
	public static float randomFloat(float min, float max) {
		return (((defaultRNG.nextFloat() * max) - min) + min);
	}
	
	/**
	 * @param max Maximum number to generate.
	 * @return Randomly generated number.
	 */
	public static int randomInt(int max) {
		return defaultRNG.nextInt(max);
	}
	
	/**
	 * @param percentage The chance of getting a True value.
	 * @return True if the generated number is less than or equal to the percentage given,
	 * otherwise, False.
	 */
	public static boolean randomChance(float percentage) {
		boolean res = false;
		float n = randomFloat(100);
		
		if (n <= percentage) {
			res = true;
		}
		
		return res;
	}
	
}
