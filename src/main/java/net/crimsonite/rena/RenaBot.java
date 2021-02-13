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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.crimsonite.rena.commands.info.GuildinfoCommand;
import net.crimsonite.rena.commands.info.PingCommand;
import net.crimsonite.rena.commands.info.StatusCommand;
import net.crimsonite.rena.commands.info.UserinfoCommand;
import net.crimsonite.rena.commands.roleplay.DailyCommand;
import net.crimsonite.rena.database.DBConnection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class RenaBot {
	
	public static String prefix;
	public static String alternativePrefix;
	public static String ownerID;
	public static String hostName;
	public static long startup;

	final static Logger logger = LoggerFactory.getLogger(RenaBot.class);
	
	protected RenaBot() {
		logger.info("Preparing bot for activation...");
		
		startup = System.currentTimeMillis();
		
		List<String> list;
		try {
			list = Files.readAllLines(Paths.get("config.txt"));

			ownerID = list.get(1);
			prefix = list.get(3);
			alternativePrefix = list.get(4);
			hostName = list.get(5);
	        
			JDA jda = JDABuilder.createDefault(list.get(0))
				.setStatus(OnlineStatus.ONLINE)
				.setActivity(Activity.playing("loading..."))
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.addEventListeners(
						new PingCommand(),
						new UserinfoCommand(),
						new GuildinfoCommand(),
						new StatusCommand(),
						new DailyCommand()
						)
				.build();
			
			if (jda.awaitReady() != null) {
				logger.info("{} activated in {} second(s).", new Object[] {jda.getSelfUser().getName(), ((System.currentTimeMillis()-startup)/1000)});
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			logger.error("Connection has been interrupted.");
		}
		catch (IllegalArgumentException e) {
			logger.error("Failed to login, try checking if the Token and Intents are provided.");
		}
		catch (LoginException e) {
			logger.error("Failed to login, try checking if the provided Token is valid.");
		}
	}
	
	// Execute program
	public static void main(String[] args) {
		logger.info("Starting up...");
		
		new RenaBot();
		DBConnection.conn();
	}

}
