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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Cooldown {
	
	private static ConcurrentHashMap<String, Long> cooldownCache = new ConcurrentHashMap<>();
	
	public static ConcurrentHashMap<String, Long> getCooldownCache() {
		return cooldownCache;
	}
	
	public static long getRemainingCooldown(String UID, String command) {
		String key = UID + "-" + command;
		return cooldownCache.get(key);
	}
	
	public static void removeCooldown(String UID, String command) {
		String key = UID + "-" + command;
		cooldownCache.remove(key);
	}
	
	public static void setCooldown(String UID, String command, long cooldown) {
		String key = UID + "-" + command;
		long cooldownDuration = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + cooldown;
		cooldownCache.put(key, cooldownDuration);
	}

}
