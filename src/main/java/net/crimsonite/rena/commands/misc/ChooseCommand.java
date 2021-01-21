package net.crimsonite.rena.commands.misc;

import net.crimsonite.rena.RenaBot;
import net.crimsonite.rena.utils.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ChooseCommand extends Command{

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		
		if (args.length <= 1) {
			event.getChannel().sendMessage("I choose **nothing**...").queue();
		}
		else {
			String choice = args[(int)(Math.random()*args.length)];
			event.getChannel().sendMessageFormat("I choose **%s**.", choice).queue();
		}
	}

	@Override
	public String getCommandName() {
		return RenaBot.prefix + "choose";
	}
	
	@Override
	public boolean isOwnerCommand() {
		return false;
	}

}
