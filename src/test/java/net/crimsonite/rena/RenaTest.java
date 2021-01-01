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

package net.crimsonite.rena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;

public class RenaTest {
	
	private static String token;
	
	public static void main(String[] args) {
		List<String> list;
		try {
			list = Files.readAllLines(Paths.get("config.txt"));
			token = list.get(0);
			
			JDABuilder.createLight(token).build();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (LoginException e) {
			e.printStackTrace();
		}
		
	}

}
