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

import net.crimsonite.rena.commands.ShutdownCmd;
import net.crimsonite.rena.commands.info.GuildInfoCmd;
import net.crimsonite.rena.commands.info.PingCmd;
import net.crimsonite.rena.commands.info.ShardInfoCmd;
import net.crimsonite.rena.commands.info.StatusCmd;
import net.crimsonite.rena.commands.info.UserInfoCmd;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class Rena {
	
	public static String token;
	public static String prefix;
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
			
			client.setStatus(OnlineStatus.ONLINE);
			client.setActivity(Activity.watching("over you"));
			client.setOwnerId(ownerID);
			client.setEmojis("\u2714", "\u26A0", "\u274c");
			client.setPrefix(prefix);
			client.addCommands(
					new GuildInfoCmd(),
					new PingCmd(),
					new ShardInfoCmd(),
					new StatusCmd(),
					new UserInfoCmd(),
					
					new ShutdownCmd()
					);
	        
	        JDABuilder.createDefault(token)
			.setStatus(OnlineStatus.DO_NOT_DISTURB)
			.setActivity(Activity.playing("loading..."))
			.addEventListeners(waiter, client.build())
			.build();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (LoginException e) {
			e.printStackTrace();
		}
        
		logger.info("Bot activated in " + (System.currentTimeMillis() - startup) + "ms.");
	}
	
	public static void main(String[] args) {
		new Rena();
	}
	
}
