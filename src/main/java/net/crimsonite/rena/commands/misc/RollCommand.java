package net.crimsonite.rena.commands.misc;

import java.util.Random;

import net.crimsonite.rena.utils.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RollCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		Random rng = new Random();
		
		if (args.length == 2) {
			try {
				int face = Integer.parseInt(args[1]);
				int result = rng.nextInt(face-1)+1;
				channel.sendMessageFormat(":game_die: %d (1-%d)", result, face).queue();
			}
			catch (NumberFormatException ignored) {
				channel.sendMessageFormat("Sorry, I can't roll that for you").queue();
			}
		}
		else {
			int result = rng.nextInt(6-1)+1;
			channel.sendMessageFormat(":game_die: %d (1-6)", result).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "roll";
	}

	@Override
	public long cooldown() {
		return 5;
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

}
