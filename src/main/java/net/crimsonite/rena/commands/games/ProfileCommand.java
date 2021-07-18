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

import java.awt.Color;
import java.util.List;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.crimsonite.rena.entities.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ProfileCommand extends Command {
	
	private static void sendEmbed(MessageReceivedEvent event, User user) {
		Color roleColor = event.getGuild().retrieveMember(user).complete().getColor();
		Player player = new Player(user.getId());
		
		String defaultUserStatus = "";
		String defaultUserBirthday = I18n.getMessage(user.getId(), "game.profile.embed.no_birthday");
		String userStatus = null;
		String userBirthday = null;
		
		try {
			userStatus = DBReadWrite.getValueString(Table.USERS, user.getId(), "Status");
			userBirthday = DBReadWrite.getValueString(Table.USERS, user.getId(), "Birthday");
			
			if (userStatus == null) {
				userStatus = defaultUserStatus;
			}
			if(userBirthday == null) {
				userBirthday = defaultUserBirthday;
			}
		}
		catch (NullPointerException ignored) {}
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(I18n.getMessage(user.getId(), "game.profile.embed.title").formatted(user.getName()))
				.setDescription(userStatus)
				.setThumbnail(user.getEffectiveAvatarUrl())
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.rep"), String.valueOf(DBReadWrite.getValueInt(Table.PLAYERS, user.getId(), "REP")), false)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.level"), String.valueOf(player.getLvl()), false)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.exp"), String.valueOf(player.getExp()), false)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.money"), String.valueOf(DBReadWrite.getValueInt(Table.PLAYERS, user.getId(), "MONEY")), true)
				.addBlankField(false)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.hp"), String.valueOf(player.getHp()), true)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.mp"), String.valueOf(player.getMp()), true)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.atk"), String.valueOf(player.getAtk()), true)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.def"), String.valueOf(player.getDef()), true)
				.addBlankField(false)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.vit"), String.valueOf(player.getVit()), true)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.str"), String.valueOf(player.getStr()), true)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.agi"), String.valueOf(player.getAgi()), true)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.int"), String.valueOf(player.getInt()), true)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.wis"), String.valueOf(player.getWis()), true)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.luk"), String.valueOf(player.getLuk()), true)
				.addBlankField(false)
				.addField(I18n.getMessage(user.getId(), "game.profile.embed.birthday"), userBirthday, false)
				.setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl());
		
		event.getChannel().sendMessageEmbeds(embed.build()).queue();
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		MessageChannel channel = event.getChannel();
		
		if (args.length == 1) {
			try {
				sendEmbed(event, author);
			}
			catch (NullPointerException ignored) {
				DBReadWrite.registerUser(event.getAuthor().getId());
				channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.error")).queue();
			}
		}
		else if (args.length >= 2) {
			if (args[1].equals("-set")) {
				if (args.length == 2) {
					channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.nothing_to_set")).queue();
				}
				else {
					switch (args[2]) {
						case "status":
							if (args.length >= 4) {
								String status = "";
								
								for (int i = 3; i < args.length; i++) {
									status = status.concat(args[i] + " ");
								}
								
								status = status.substring(0, status.length() - 1);
								
								DBReadWrite.modifyDataString(Table.USERS, author.getId(), "Status", status);
								
								channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.set_status_success").formatted(status)).queue();
							}
							else {
								channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.set_status_failed")).queue();
							}
							
							break;
						case "birthday":
							if (args.length >= 4) {
								String[] birthdayAsArray = args[3].split("\\-");
								String birthday = "";
								
								if (birthdayAsArray.length == 2) {
									try {
										int month = Integer.parseInt(birthdayAsArray[0]);
										int day = Integer.parseInt(birthdayAsArray[1]);
										
										if (month > 12 || month < 0) {
											channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.invalid_month")).queue();
											
											return;
										}
										if(day > 31 || day < 0) {
											channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.invalid_day")).queue();
											
											return;
										}
										
										birthday = "%1$s-%2$s".formatted(month, day);
									}
									catch (NumberFormatException ignored) {
										channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.illegal_birthday_number")).queue();
										
										return;
									}
								}
								else {
									channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.illegal_birthday")).queue();
									
									return;
								}
																
								DBReadWrite.modifyDataString(Table.USERS, author.getId(), "Birthday", birthday);
								
								channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.set_birthday_success").formatted(birthday)).queue();
							}
							else {
								channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.set_birthday_failed").formatted(args[2])).queue();
							}
							
							break;
						default:
							channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.cannot_set")).queue();
							
							break;
					}
				}
			}
			else {
				if (!event.getMessage().getMentionedMembers().isEmpty()) {
					try {
						User user = event.getMessage().getMentionedUsers().get(0);
						sendEmbed(event, user);
					}
					catch (NullPointerException ignored) {
						DBReadWrite.registerUser(event.getMessage().getMentionedUsers().get(0).getId());
						channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.error")).queue();
					}
				}
				else {
					List<Member> listedMembers = FinderUtil.findMembers(args[1], event.getGuild());
					
					if (listedMembers.isEmpty()) {
						channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.player_not_found")).queue();
						event.getGuild().loadMembers();
					}
					else {
						try {
							User user = listedMembers.get(0).getUser();
							sendEmbed(event, user);
						}
						catch (NullPointerException ignored) {
							DBReadWrite.registerUser(listedMembers.get(0).getId());
							channel.sendMessage(I18n.getMessage(author.getId(), "game.profile.error")).queue();
						}
					}
				}
			}
		}
	}

	@Override
	public String getCommandName() {
		return "profile";
	}
	
	@Override
	public String getCommandCategory() {
		return "Games";
	}

	@Override
	public long cooldown() {
		return 5;
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
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
