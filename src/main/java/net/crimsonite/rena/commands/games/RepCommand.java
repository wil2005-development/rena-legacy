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

package net.crimsonite.rena.commands.games;

import java.util.List;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.Cooldown;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RepCommand extends Command {
	
	private boolean shouldRemoveCooldown = false;
	private String playerId;

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
		MessageChannel channel = event.getChannel();
		
		this.playerId = author.getId();
		
		try {
			if (mentionedMembers.isEmpty()) {
				channel.sendMessage(I18n.getMessage(author.getId(), "game.rep.no_mention")).queue();
				
				this.shouldRemoveCooldown = true;
			}
			else {
				Member member = mentionedMembers.get(0);
				
				if (member.getUser() == author) {
					channel.sendMessage(I18n.getMessage(author.getId(), "game.rep.self_rep")).queue();
					
					this.shouldRemoveCooldown = true;
				}
				else {
					DBReadWrite.incrementValue(Table.PLAYERS, member.getId(), "REP", 1);
					
					channel.sendMessage(I18n.getMessage(author.getId(), "game.rep.give_rep").formatted(author.getName(), member.getEffectiveName())).queue();
				}
			}
		}
		catch (NullPointerException e) {
			channel.sendMessage(I18n.getMessage(author.getId(), "game.rep.user_not_found")).queue();
			
			this.shouldRemoveCooldown = true;
		}
	}
	
	@Override
	public void postCommandEvent() {
		if (this.shouldRemoveCooldown) {
			Cooldown.removeCooldown(this.playerId, getCommandName());
		}
	}

	@Override
	public String getCommandName() {
		return "rep";
	}

	@Override
	public String getCommandCategory() {
		return "Games";
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

	@Override
	public long cooldown() {
		return 86_400;
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return null;
	}

}
