package net.crimsonite.rena.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.crimsonite.rena.RenaBot;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter {
	
	private ConcurrentHashMap<String, Long> cooldownCache = new ConcurrentHashMap<>();
	
	public abstract void execute(MessageReceivedEvent event, String[] args);
	public abstract String getCommandName();
	public abstract long cooldown();
	
	// Reserved for future use
	public abstract boolean isOwnerCommand();
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		User author = event.getAuthor();
		
		if (author.isBot()) {
			return;
		}
		
		if (cooldownCache.containsKey(author.getId())) {
			long remainingCooldownShortened = remainingCooldown(author.getId())-TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
			
			if (remainingCooldownShortened > 0) {
				event.getChannel().sendMessageFormat("**Oi oi! Slow down!!!** *This command is on cooldown for* `%d`s.", remainingCooldownShortened).queue();
				
				return;
			}
			else if (remainingCooldownShortened <= 0) {
				removeCooldown(author.getId());
				
				if (containsCommand(event.getMessage())) {
		        	execute(event, commandArgs(event.getMessage()));
		        	setCooldown(author.getId());
		        }
			}
		}
		else {
			if (containsCommand(event.getMessage())) {
	        	execute(event, commandArgs(event.getMessage()));
	        	setCooldown(author.getId());
	        }
		}
	}
	
	protected long remainingCooldown(String UID) {
		return cooldownCache.get(UID);
	}
	
	protected void setCooldown(String UID) {
		long cooldownDuration = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + this.cooldown();
		cooldownCache.put(UID, cooldownDuration);
	}
	
	protected void removeCooldown(String UID) {
		cooldownCache.remove(UID);
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
