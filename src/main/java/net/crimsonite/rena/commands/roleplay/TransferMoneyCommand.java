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

package net.crimsonite.rena.commands.roleplay;

import java.util.List;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBReadWrite;
import net.crimsonite.rena.database.DBReadWrite.Table;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TransferMoneyCommand extends Command {
	
	private static void transferMoney(User author, Member member, int amount, MessageChannel channel) {
		int amountAfterTax = Math.round((float) amount - (amount * 0.02f));
		
		if (amount <= 0 || amount >= 100_000) {
			channel.sendMessage(I18n.getMessage(author.getId(), "roleplay.transfer.invalid_amount")).queue();
			
			return;
		}
		
		try {
			int balance = DBReadWrite.getValueInt(Table.PLAYERS, author.getId(), "MONEY");
			DBReadWrite.getValueInt(Table.PLAYERS, member.getId(), "MONEY");
			
			if (balance < amount) {
				channel.sendMessage(I18n.getMessage(author.getId(), "roleplay.transfer.not_enough_money")).queue();
				
				return;
			}
			else {
				DBReadWrite.decrementValue(Table.PLAYERS, author.getId(), "MONEY", amount);
				DBReadWrite.incrementValue(Table.PLAYERS, member.getId(), "MONEY", amountAfterTax);
				
				channel.sendMessage(I18n.getMessage(author.getId(), "roleplay.transfer.sent").formatted(amountAfterTax, member.getEffectiveName(), 2)).queue();
			}
		}
		catch (NullPointerException e) {
			channel.sendMessage(I18n.getMessage(author.getId(), "roleplay.transfer.failed")).queue();
		}
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		if (args.length >= 3) {
			Member member;
			
			int amount = Integer.parseInt(args[1]);
			
			if (!event.getMessage().getMentionedMembers().isEmpty()) {
				member = event.getMessage().getMentionedMembers().get(0);
				
				transferMoney(author, member, amount, channel);
			}
			else {
				List<Member> listedMembers = FinderUtil.findMembers(args[2], event.getGuild());
				
				if (listedMembers.isEmpty()) {
					channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "info.user_info.user_not_found")).queue();
					event.getGuild().loadMembers();
				}
				else {
					member = listedMembers.get(0);
					
					transferMoney(author, member, amount, channel);
				}
			}
		}
	}

	@Override
	public String getCommandName() {
		return "transfer";
	}

	@Override
	public String getCommandCategory() {
		return "Roleplay";
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

	@Override
	public long cooldown() {
		return 10;
	}

}
