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

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class UnbanCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		Member author = event.getMember();
		MessageChannel channel = event.getChannel();
		
		try {
			if (author.hasPermission(Permission.BAN_MEMBERS)) {	
				User user;
				
				if (args.length == 1) {
					channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "moderation.unban.no_id_provided")).queue();
				}
				else if (args.length == 2) {
					user = event.getJDA().retrieveUserById(args[1]).complete();
					
					event.getGuild().unban(user).complete();
					channel.sendMessageFormat(I18n.getMessage(event.getAuthor().getId(), "moderation.unban.unban_success"), user.getName()).queue();
				}
				else {
					user = event.getJDA().retrieveUserById(args[1]).complete();
					String reason = args[2];
					event.getGuild().unban(user).reason(reason).complete();
					channel.sendMessageFormat(I18n.getMessage(event.getAuthor().getId(), "moderation.unban.unban_with_reason_success"), user.getName(), reason).queue();
				}
			}
			else {
				channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "moderation.unban.no_permission").formatted(":warning:")).queue();
			}
		}
		catch (IllegalArgumentException ignored) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "moderation.unban.invalid_id")).queue();
		}
		catch (ErrorResponseException ignored) {
			channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "moderation.unban.user_not_found")).queue();
		}
	}
	
	@Override
	public String getCommandName() {
		return "unban";
	}
	
	@Override
	public String getCommandCategory() {
		return "Moderation";
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
