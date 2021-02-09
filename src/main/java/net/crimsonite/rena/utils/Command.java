package net.crimsonite.rena.utils;

import net.crimsonite.rena.RenaBot;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter {
	
	public abstract void execute(MessageReceivedEvent event, String[] args);
	public abstract String getCommandName();
	
	// Reserved for future use
	public abstract boolean isOwnerCommand();
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}
        if (containsCommand(event.getMessage())) {
        	execute(event, commandArgs(event.getMessage()));
        }
	}
	
	protected boolean containsCommand(Message message) {
		return (RenaBot.prefix+getCommandName()).equalsIgnoreCase(commandArgs(message)[0]);
	}
	
	protected String[] commandArgs(Message message) {
		return commandArgs(message.getContentDisplay());
	}
	
	protected String[] commandArgs(String string) {
		return string.split("\\s+");
	}
	
	protected Message sendMessage(MessageReceivedEvent event, Message message) {
		if (event.isFromType(ChannelType.PRIVATE)) {
			return event.getPrivateChannel().sendMessage(message).complete();
		}
		else {
			return event.getTextChannel().sendMessage(message).complete();
		}
	}
	
	protected Message sendMessage(MessageReceivedEvent event, String message) {
		return sendMessage(event, new MessageBuilder().append(message).build());
	}
}
