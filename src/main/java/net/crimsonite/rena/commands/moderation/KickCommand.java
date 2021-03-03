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

package net.crimsonite.rena.commands.moderation;

import java.util.List;

import net.crimsonite.rena.commands.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class KickCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		Member author = event.getMember();
		List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
		MessageChannel channel = event.getChannel();
		
		if (mentionedMembers.isEmpty()) {
			channel.sendMessage("**You have to mention a user to kick.**").queue();
		}
		else if (mentionedMembers.size() > 1) {
			channel.sendMessage("**Hold on! You can only kick members one at a time.**").queue();
		}
		else {
			if (author.hasPermission(Permission.KICK_MEMBERS)) {
				try {
					if (args.length > 2) {
						String reason = args[2];
						
						mentionedMembers.get(0).kick(reason).complete();
						channel.sendMessageFormat("**Successfully kicked %s! Reason: %s**", mentionedMembers.get(0).getUser().getName(), reason).queue();
					}
					else {
						mentionedMembers.get(0).kick().complete();
						channel.sendMessageFormat("**Successfully kicked %s!**", mentionedMembers.get(0).getUser().getName()).queue();
					}
				}
				catch (HierarchyException ignored) {
					channel.sendMessage("**Sorry, but you can't kick that person.**").queue();
				}
			}
			else {
				channel.sendMessage("**Sorry, but you dont have the permission to do that.\nRequired: KICK_MEMBERS permission**").queue();
			}
		}
	}

	@Override
	public String getCommandName() {
		return "kick";
	}
	
	@Override
	public boolean isOwnerCommand() {
		return false;
	}

	@Override
	public long cooldown() {
		return 0;
	}

}
