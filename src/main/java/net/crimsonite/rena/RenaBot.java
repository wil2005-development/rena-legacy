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

import java.io.File;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.commands.info.GuildinfoCommand;
import net.crimsonite.rena.commands.info.PingCommand;
import net.crimsonite.rena.commands.info.RoleinfoCommand;
import net.crimsonite.rena.commands.info.StatusCommand;
import net.crimsonite.rena.commands.info.UserinfoCommand;
import net.crimsonite.rena.commands.misc.RollCommand;
import net.crimsonite.rena.commands.moderation.KickCommand;
import net.crimsonite.rena.commands.roleplay.DailyCommand;
import net.crimsonite.rena.commands.roleplay.ExpeditionCommand;
import net.crimsonite.rena.commands.roleplay.HuntCommand;
import net.crimsonite.rena.commands.roleplay.LootCommand;
import net.crimsonite.rena.commands.roleplay.ProfileCommand;
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
	public static String hostName;
	public static long ownerID;
	public static long startup;

	final static Logger logger = LoggerFactory.getLogger(RenaBot.class);
	
	protected RenaBot() {
		logger.info("Preparing bot for activation...");
		
		startup = System.currentTimeMillis();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode configRoot = mapper.readTree(new File("./config.json"));
			
			prefix = configRoot.get("PREFIX").asText();
			alternativePrefix = configRoot.get("ALTERNATIVE_PREFIX").asText();
			hostName = configRoot.get("HOST").asText();
			ownerID = configRoot.get("OWNER_ID").asLong();
	        
			JDA jda = JDABuilder.createDefault(configRoot.get("TOKEN").asText())
				.setStatus(OnlineStatus.ONLINE)
				.setActivity(Activity.playing("loading..."))
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.addEventListeners(
						new UserinfoCommand(),
						new PingCommand(),
						new GuildinfoCommand(),
						new RoleinfoCommand(),
						new StatusCommand(),
						
						new KickCommand(),
						
						new ExpeditionCommand(),
						new DailyCommand(),
						new HuntCommand(),
						new LootCommand(),
						new ProfileCommand(),
						
						new RollCommand()
						
						// new ModifyAttributesCommand()
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
