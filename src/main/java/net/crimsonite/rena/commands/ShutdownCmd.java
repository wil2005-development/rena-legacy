package net.crimsonite.rena.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

import net.crimsonite.rena.Rena;

@CommandInfo(
	    name = {"shutdown"},
	    description = "Disconnects the bot connections."
	)
public class ShutdownCmd extends Command{
	
	final static Logger logger = LoggerFactory.getLogger(Rena.class);
	
	public ShutdownCmd() {
		this.name = "shutdown";
		this.hidden = true;
		this.ownerCommand = true;
        this.guildOnly = false;
	}
	
	protected void execute(CommandEvent event) {
		event.reactWarning();
		logger.warn("Shutting down...");
		
		event.getJDA().shutdown();
		logger.info("Successfully closed all connections!");
	}

}
