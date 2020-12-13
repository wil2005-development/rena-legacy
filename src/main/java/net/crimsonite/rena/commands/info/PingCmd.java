package net.crimsonite.rena.commands.info;

import java.time.temporal.ChronoUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

@CommandInfo(
		name = {"ping"},
		description = "Shows the client and websocket ping."
		)
public class PingCmd extends Command{
	
	private static long ping;
	private static long websocket;
	
	public PingCmd() {
		this.name = "ping";
		this.aliases = new String[] {"latency"};
		this.category = new Category("Informations");
		this.help = "Shows the client and websocket ping";
		this.guildOnly = false;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		if (event.getAuthor().isBot())
			return;
					
		event.reply("Requesting...", msg -> {
            ping = event.getMessage().getTimeCreated().until(msg.getTimeCreated(), ChronoUnit.MILLIS);
            websocket = event.getJDA().getGatewayPing();
            msg.editMessageFormat("**Ping: **%dms | **Websocket: **%dms", ping, websocket).queue();
        });
	}

}
