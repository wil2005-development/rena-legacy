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

import java.security.SecureRandom;
import java.util.Random;

import javax.annotation.Nullable;

public class RandomGenerator {

    private static Random defaultRNG(@Nullable byte[] seed) {
        if (seed != null) {
            return new SecureRandom(seed);
        }
        return new SecureRandom();
    }

    /**
     * Generates a unique seed as byte array based on the current time.
     *
     * @return Pseudorandom generated seed.
     */
    public static byte[] generateSeedFromCurrentTime() {
        return String.valueOf(System.currentTimeMillis()).getBytes();
    }

    /**
     * @param min Minimum number to generate.
     * @param max Maximum number to generate.
     * @return Randomly generated number.
     */
    public static int randomInt(int min, int max) {
        return defaultRNG(null).nextInt(max - min) + min;
    }

    /**
     * @param min  Minimum number to generate.
     * @param max  Maximum number to generate.
     * @param seed (nullable) a key used to generate more entropy.
     * @return Randomly generated number.
     */
    public static int randomInt(int min, int max, @Nullable byte[] seed) {
        if (seed != null) {
            return defaultRNG(seed).nextInt(max - min) + min;
        }
        return defaultRNG(null).nextInt(max - min) + min;
    }

    /**
     * @param max Maximum number to generate.
     * @return Randomly generated number.
     */
    public static int randomInt(int max) {
        return defaultRNG(null).nextInt(max);
    }

    /**
     * @param max  Maximum number to generate.
     * @param seed (nullable) a key used to generate more entropy.
     * @return Randomly generated number.
     */
    public static int randomInt(int max, @Nullable byte[] seed) {
        if (seed != null) {
            return defaultRNG(seed).nextInt(max);
        }
        return defaultRNG(null).nextInt(max);
    }

    /**
     * @param max Maximum number to generate.
     * @return Randomly generated number.
     */
    public static float randomFloat(float max) {
        return defaultRNG(null).nextFloat() * max;
    }

    /**
     * @param max  Maximum number to generate.
     * @param seed (nullable) a key used to generate more entropy.
     * @return Randomly generated number.
     */
    public static float randomFloat(float max, @Nullable byte[] seed) {
        if (seed != null) {
            return defaultRNG(seed).nextFloat() * max;
        }
        return defaultRNG(null).nextFloat() * max;
    }

    /**
     * @param min Minimum number to generate.
     * @param max Maximum number to generate.
     * @return Randomly generated number.
     */
    public static float randomFloat(float min, float max) {
        return (((defaultRNG(null).nextFloat() * max) - min) + min);
    }

    /**
     * @param min  Minimum number to generate.
     * @param max  Maximum number to generate.
     * @param seed (nullable) a key used to generate more entropy.
     * @return Randomly generated number.
     */
    public static float randomFloat(float min, float max, @Nullable byte[] seed) {
        if (seed != null) {
            return (((defaultRNG(seed).nextFloat() * max) - min) + min);
        }
        return (((defaultRNG(null).nextFloat() * max) - min) + min);
    }

    /**
     * @param max Maximum number to generate.
     * @return Randomly generated number.
     */
    public static double randomDouble(double max) {
        return defaultRNG(null).nextDouble() * max;
    }

    /**
     * @param max  Maximum number to generate.
     * @param seed (nullable) a key used to generate more entropy.
     * @return Randomly generated number.
     */
    public static double randomDouble(double max, @Nullable byte[] seed) {
        if (seed != null) {
            return defaultRNG(seed).nextDouble() * max;
        }
        return defaultRNG(null).nextDouble() * max;
    }

    /**
     * @param min Minimum number to generate.
     * @param max Maximum number to generate.
     * @return Randomly generated number.
     */
    public static double randomDouble(double min, double max) {
        return (((defaultRNG(null).nextDouble() * max) - min) + min);
    }

    /**
     * @param min  Minimum number to generate.
     * @param max  Maximum number to generate.
     * @param seed (nullable) a key used to generate more entropy.
     * @return Randomly generated number.
     */
    public static double randomDouble(double min, double max, @Nullable byte[] seed) {
        if (seed != null) {
            return (((defaultRNG(seed).nextDouble() * max) - min) + min);
        }
        return (((defaultRNG(null).nextDouble() * max) - min) + min);
    }

    /**
     * @param percentage The chance of getting a True value.
     * @return True if the generated number is less than or equal to the percentage given,
     * otherwise, False.
     */
    public static boolean randomChance(double percentage) {
        boolean res = false;
        double n = randomDouble(100);

        if (n <= percentage) {
            res = true;
        }

        return res;
    }

    /**
     * @param percentage The chance of getting a True value.
     * @param seed       (nullable) a key used to generate more entropy.
     * @return True if the generated number is less than or equal to the percentage given,
     * otherwise, False.
     */
    public static boolean randomChance(double percentage, @Nullable byte[] seed) {
        boolean res = false;
        double n = randomDouble(100);

        if (seed != null) {
            n = randomDouble(100, seed);
        }

        if (n <= percentage) {
            res = true;
        }

        return res;
    }

    /**
     * @param percentage The chance of getting a True value.
     * @return True if the generated number is less than or equal to the percentage given,
     * otherwise, False.
     */
    public static boolean randomChance(float percentage) {
        return randomChance(percentage, null);
    }

    /**
     * @param percentage The chance of getting a True value.
     * @param seed       (nullable) a key used to generate more entropy.
     * @return True if the generated number is less than or equal to the percentage given,
     * otherwise, False.
     */
    public static boolean randomChance(float percentage, @Nullable byte[] seed) {
        if (seed != null) {
            return randomChance(percentage, seed);
        }
        return randomChance(percentage);
    }

}
