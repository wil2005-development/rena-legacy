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
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.commands.dev.ModifyAttributesCommand;
import net.crimsonite.rena.commands.dev.ShutdownCommand;
import net.crimsonite.rena.commands.dev.StatusReportCommand;
import net.crimsonite.rena.commands.imageboard.DanbooruCommand;
import net.crimsonite.rena.commands.imageboard.SafebooruCommand;
import net.crimsonite.rena.commands.info.AvatarCommand;
import net.crimsonite.rena.commands.info.GuildinfoCommand;
import net.crimsonite.rena.commands.info.HelpCommand;
import net.crimsonite.rena.commands.info.PingCommand;
import net.crimsonite.rena.commands.info.RoleinfoCommand;
import net.crimsonite.rena.commands.info.ShardInfoCommand;
import net.crimsonite.rena.commands.info.StatusCommand;
import net.crimsonite.rena.commands.info.UserinfoCommand;
import net.crimsonite.rena.commands.misc.DiceCommand;
import net.crimsonite.rena.commands.misc.EightBallCommand;
import net.crimsonite.rena.commands.moderation.BanCommand;
import net.crimsonite.rena.commands.moderation.KickCommand;
import net.crimsonite.rena.commands.moderation.SetGuildPrefixCommand;
import net.crimsonite.rena.commands.moderation.UnbanCommand;
import net.crimsonite.rena.commands.roleplay.DailyCommand;
import net.crimsonite.rena.commands.roleplay.ExpeditionCommand;
import net.crimsonite.rena.commands.roleplay.HuntCommand;
import net.crimsonite.rena.commands.roleplay.InsightCommand;
import net.crimsonite.rena.commands.roleplay.InventoryCommand;
import net.crimsonite.rena.commands.roleplay.LootCommand;
import net.crimsonite.rena.commands.roleplay.ProfileCommand;
import net.crimsonite.rena.commands.roleplay.TransferMoneyCommand;
import net.crimsonite.rena.commands.roleplay.UseItemCommand;
import net.crimsonite.rena.commands.userpreference.PreferenceCommand;
import net.crimsonite.rena.database.DBConnection;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class RenaBot {
	
	public static final long startup = System.currentTimeMillis();
	
	public static HelpCommand commandRegistry = new HelpCommand();
	public static DefaultShardManagerBuilder jdaBuilder;
	
	private static ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(RenaBot.class);
	
	private void generateConfigFile() {
		logger.info("Generating config file from templates...");
		
		try {			
			JsonNode templateFileAsTree = mapper.readTree(getClass().getClassLoader().getResourceAsStream("templates/config.json"));
			Object templateFileAsObject = mapper.treeToValue(templateFileAsTree, Object.class);
			
			mapper.writeValue(new File("config.json"), templateFileAsObject);
			
			logger.info("Successfuly made a config file!");
			logger.info("Fill them up before executing again.");
			
			System.exit(0);
		}
		catch (IOException ignored) {
			logger.error("Failed to generate config file.");
		}
	}
	
	protected RenaBot() {
		logger.info("Preparing bot for activation...");
				
		try {
			jdaBuilder = DefaultShardManagerBuilder.createDefault(RenaConfig.TOKEN)
				.setStatus(OnlineStatus.ONLINE)
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.addEventListeners(
						// Info Commands
						commandRegistry.registerCommand(new AvatarCommand()),
						commandRegistry.registerCommand(new UserinfoCommand()),
						commandRegistry.registerCommand(new GuildinfoCommand()),
						commandRegistry.registerCommand(new HelpCommand()),
						commandRegistry.registerCommand(new PingCommand()),
						commandRegistry.registerCommand(new RoleinfoCommand()),
						commandRegistry.registerCommand(new ShardInfoCommand()),
						commandRegistry.registerCommand(new StatusCommand()),
						
						// Moderation Commands
						commandRegistry.registerCommand(new UnbanCommand()),
						commandRegistry.registerCommand(new BanCommand()),
						commandRegistry.registerCommand(new KickCommand()),
						commandRegistry.registerCommand(new SetGuildPrefixCommand()),
						
						// Miscellaneous Commands
						commandRegistry.registerCommand(new EightBallCommand()),
						commandRegistry.registerCommand(new DiceCommand()),
						
						// Imageboard Commands
						commandRegistry.registerCommand(new DanbooruCommand()),
						commandRegistry.registerCommand(new SafebooruCommand()),
						
						// Roleplaying Commands
						commandRegistry.registerCommand(new ExpeditionCommand()),
						commandRegistry.registerCommand(new InsightCommand()),
						commandRegistry.registerCommand(new InventoryCommand()),
						commandRegistry.registerCommand(new UseItemCommand()),
						commandRegistry.registerCommand(new DailyCommand()),
						commandRegistry.registerCommand(new HuntCommand()),
						commandRegistry.registerCommand(new LootCommand()),
						commandRegistry.registerCommand(new ProfileCommand()),
						commandRegistry.registerCommand(new TransferMoneyCommand()),
						
						// User Preference Commands
						commandRegistry.registerCommand(new PreferenceCommand()),
						
						// Developer/Debug Command
						new ModifyAttributesCommand(),
						new ShutdownCommand(),
						new StatusReportCommand(),
						
						// Event Listener
						new ReadyListener()
						);
			
			if (RenaConfig.isSharding()) {
				int totalShards = RenaConfig.getTotalShards();
				
				logger.info("Loading (%d) shards...".formatted(totalShards));
				
				List<Integer> shardIds = new ArrayList<>();
				
				for (int i = 0; i < totalShards; i++) {
					shardIds.add(i);
				}
				
				jdaBuilder.setShardsTotal(totalShards)
							.setShards(shardIds);
			}
			
			jdaBuilder.build();
		}
		catch (NullPointerException e) {
			logger.error("A config variable returned a null value.");
			
			generateConfigFile();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			logger.error("Failed to login, try checking if the Token and config variables are provided correctly.");
		}
		catch (LoginException e) {
			logger.error("Failed to login, try checking if the provided Token is valid.");
		}
	}
	
	public static void main(String[] args) {
		logger.info("Starting up...");
		
		new RenaBot();
		
		try {
			DBConnection.conn();
		}
		catch (Exception ignored) {
			logger.warn("Couldn't connect to database. Some commands might fail, but the bot will remain active.");
		}
	}

}
