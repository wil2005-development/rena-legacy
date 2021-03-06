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

package net.crimsonite.rena.commands;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.crimsonite.rena.RenaBot;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter {
	
	final static Logger logger = LoggerFactory.getLogger(Command.class);
	
	private ConcurrentHashMap<String, Long> cooldownCache = new ConcurrentHashMap<>();
	
	private static long timesCommandUsed = 0;
	
	public abstract void execute(MessageReceivedEvent event, String[] args);
	public abstract boolean isOwnerCommand();
	public abstract long cooldown();
	public abstract String getCommandName();
	
	public static long getTimesCommandUsed() {
		return timesCommandUsed;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		User author = event.getAuthor();
		
		if (author.isBot())
			return;
		
		if (isOwnerCommand()) {
			if (author.getIdLong() != RenaBot.ownerID) {
				return;
			}
		}
		
		if (containsCommand(event.getMessage())) {
			String command = getCommandName();
			
			timesCommandUsed++;
			
			if (cooldownCache.containsKey(author.getId() + "-" + command)) {
				long remainingCooldownShortened = remainingCooldown(author.getId(), command)-TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
				
				if (remainingCooldownShortened > 0) {
					long hours = remainingCooldownShortened / 3600;
					long minutes = (remainingCooldownShortened % 3600) / 60;
					long seconds = remainingCooldownShortened % 60;
					
					event.getChannel().sendMessageFormat("**Oi oi! Slow down!!!** *This command is on cooldown for* `%02dh, %02dm, %02ds`.", hours, minutes, seconds).queue();
					
					return;
				}
				else if (remainingCooldownShortened <= 0) {
					removeCooldown(author.getId(), command);
					
					execute(event, commandArgs(event.getMessage()));
		        	setCooldown(author.getId(), getCommandName());
				}
			}
			else {
				execute(event, commandArgs(event.getMessage()));
	        	setCooldown(author.getId(), getCommandName());
			}
		}
		
		return;
	}
	
	protected long remainingCooldown(String UID, String command) {
		String key = UID + "-" + command;
		return cooldownCache.get(key);
	}
	
	protected void setCooldown(String UID, String command) {
		String key = UID + "-" + command;
		long cooldownDuration = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + this.cooldown();
		cooldownCache.put(key, cooldownDuration);
	}
	
	protected void removeCooldown(String UID, String command) {
		String key = UID + "-" + command;
		cooldownCache.remove(key);
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
