package net.crimsonite.rena.commands.roleplay;

import java.awt.Color;
import java.util.List;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBUsers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ProfileCommand extends Command {
	
	private static void sendEmbed(MessageReceivedEvent event, User user) {
		Color roleColor = event.getGuild().retrieveMember(user).complete().getColor();
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(user.getName() + "'s Profile")
				.setThumbnail(user.getEffectiveAvatarUrl())
				.addField("Rep", DBUsers.getValueString(user.getId(), "REP"), false)
				.addField("Level", DBUsers.getValueString(user.getId(), "LEVEL"), false)
				.addField("Exp", DBUsers.getValueString(user.getId(), "EXP"), false)
				.addField("Money", DBUsers.getValueString(user.getId(), "MONEY"), true)
				.addField("Hp", DBUsers.getValueString(user.getId(), "HP"), true)
				.addField("Mp", DBUsers.getValueString(user.getId(), "MP"), true)
				.addField("Atk", DBUsers.getValueString(user.getId(), "ATK"), true)
				.addField("Def", DBUsers.getValueString(user.getId(), "DEF"), true)
				.setFooter(event.getAuthor().getName(), user.getEffectiveAvatarUrl());
		
		event.getChannel().sendMessage(embed.build()).queue();
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		if (args.length == 1) {
			try {
				sendEmbed(event, event.getAuthor());
			}
			catch (NullPointerException ignored) {
				DBUsers.registerUser(event.getAuthor().getId());
				channel.sendMessage("Oops! Try again?").queue();
			}
		}
		else if (args.length >= 2) {
			if (!event.getMessage().getMentionedMembers().isEmpty()) {
				try {
					User user = event.getMessage().getMentionedUsers().get(0);
					sendEmbed(event, user);
				}
				catch (NullPointerException ignored) {
					DBUsers.registerUser(event.getMessage().getMentionedUsers().get(0).getId());
					channel.sendMessage("Oops! Try again?").queue();
				}
			}
			else {
				List<Member> listedMembers = FinderUtil.findMembers(args[1], event.getGuild());
				
				if (listedMembers.isEmpty()) {
					channel.sendMessage("*Err... I can't find that person. Try doing it again, I might've missed them*").queue();
					event.getGuild().loadMembers();
				}
				else {
					try {
						User user = listedMembers.get(0).getUser();
						sendEmbed(event, user);
					}
					catch (NullPointerException ignored) {
						DBUsers.registerUser(listedMembers.get(0).getId());
						channel.sendMessage("Oops! Try again?").queue();
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
	public long cooldown() {
		return 5;
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

}
