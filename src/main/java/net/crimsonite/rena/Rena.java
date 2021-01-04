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

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.crimsonite.rena.commands.dev.ShutdownCommand;
import net.crimsonite.rena.commands.info.GuildInfoCommand;
import net.crimsonite.rena.commands.info.PingCommand;
import net.crimsonite.rena.commands.info.ShardInfoCommand;
import net.crimsonite.rena.commands.info.StatusCommand;
import net.crimsonite.rena.commands.info.UserInfoCommand;
import net.crimsonite.rena.commands.misc.ChooseCommand;
import net.crimsonite.rena.commands.misc.SayCommand;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Rena {
	
	public static String token;
	public static String prefix;
	public static String alternativePrefix;
	public static String ownerID;
	
	public static EventWaiter waiter = new EventWaiter();
	public static CommandClientBuilder client = new CommandClientBuilder();

	final static Logger logger = LoggerFactory.getLogger(Rena.class);
	
	private Rena() {
		logger.info("Starting up...");
		
		long startup = System.currentTimeMillis();
		
		List<String> list;
		try {
			list = Files.readAllLines(Paths.get("config.txt"));
			
			token = list.get(0);
			ownerID = list.get(1);
			prefix = list.get(3);
			alternativePrefix = list.get(4);
			
			client.setStatus(OnlineStatus.ONLINE);
			client.setActivity(Activity.watching("over you"));
			client.setOwnerId(ownerID);
			client.setEmojis("\u2714", "\u26A0", "\u274c");
			client.setPrefix(prefix);
			client.setAlternativePrefix(alternativePrefix);
			client.addCommands(
					new ChooseCommand(),
					new SayCommand(),
					
					new GuildInfoCommand(),
					new PingCommand(),
					new ShardInfoCommand(),
					new StatusCommand(),
					new UserInfoCommand(),
					
					new ShutdownCommand()
					);
	        
	        JDABuilder.createDefault(token)
			.setStatus(OnlineStatus.DO_NOT_DISTURB)
			.setActivity(Activity.playing("loading..."))
			.enableIntents(GatewayIntent.GUILD_MEMBERS)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.addEventListeners(waiter, client.build())
			.build();
	        
	        logger.info("Bot activated in " + (System.currentTimeMillis() - startup) + "ms.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			logger.error("Failed to login, try checking if the Token and Intents are provided.");
		}
		catch (LoginException e) {
			logger.error("Failed to login, try checking if the provided Token is valid.");
		}
	}
	
	public static void main(String[] args) {
		new Rena();
	}
	
}
