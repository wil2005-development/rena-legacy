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

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TransferMoneyCommand extends Command {
	
	private static void transferMoney(User author, Member member, int amount, MessageChannel channel) {
		int amountAfterTax = Math.round((float) amount - (amount * 0.02f));
		
		if (amount <= 0 || amount >= 100_000) {
			channel.sendMessage(I18n.getMessage(author.getId(), "game.transfer.invalid_amount")).queue();
		}
		
		try {
			int balance = DBReadWrite.getValueInt(Table.PLAYERS, author.getId(), "MONEY");
			DBReadWrite.getValueInt(Table.PLAYERS, member.getId(), "MONEY");
			
			if (balance < amount) {
				channel.sendMessage(I18n.getMessage(author.getId(), "game.transfer.not_enough_money")).queue();
			}
			else {
				DBReadWrite.decrementValue(Table.PLAYERS, author.getId(), "MONEY", amount);
				DBReadWrite.incrementValue(Table.PLAYERS, member.getId(), "MONEY", amountAfterTax);
				
				channel.sendMessage(I18n.getMessage(author.getId(), "game.transfer.sent").formatted(amountAfterTax, member.getEffectiveName(), 2)).queue();
			}
		}
		catch (NullPointerException e) {
			channel.sendMessage(I18n.getMessage(author.getId(), "game.transfer.failed")).queue();
		}
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		try {
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
			
			else {
				channel.sendMessage(I18n.getMessage(author.getId(), "game.transfer.incomplete_args")).queue();			}
		}
		catch (NumberFormatException e) {
			channel.sendMessage(I18n.getMessage(author.getId(), "game.transfer.invalid_number").formatted(args[1])).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "transfer";
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
		return 10;
	}

}
