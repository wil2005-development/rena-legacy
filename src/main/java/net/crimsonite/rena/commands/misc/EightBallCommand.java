package net.crimsonite.rena.commands.misc;

import java.util.Random;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EightBallCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		Random rng = new Random();
		
		String[] possibleResponses = I18n.getStringArray(event.getAuthor().getId(), "misc.8ball.answers");
		String answer = possibleResponses[rng.nextInt(possibleResponses.length)];
		
		if (args.length == 1) {
			channel.sendMessage(I18n.getMessage("misc.8ball.no_question")).queue();
		}
		else {
			channel.sendMessage(answer).reference(event.getMessage()).mentionRepliedUser(false).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "8ball";
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

	@Override
	public long cooldown() {
		return 5;
	}

}
