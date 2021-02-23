package net.crimsonite.rena.commands.roleplay;

import java.awt.Color;

import net.crimsonite.rena.database.DBUsers;
import net.crimsonite.rena.utils.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ProfileCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		User author = event.getAuthor();
		
		if (event.getMessage().getMentionedUsers().isEmpty()) {
			try {
				Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
				EmbedBuilder embed = new EmbedBuilder()
						.setColor(roleColor)
						.setTitle(author.getName() + "'s Profile")
						.setThumbnail(author.getEffectiveAvatarUrl())
						.addField("Level", DBUsers.getValueString(author.getId(), "LEVEL"), false)
						.addField("Exp", DBUsers.getValueString(author.getId(), "EXP"), true)
						.addField("Rep", DBUsers.getValueString(author.getId(), "REP"), true)
						.addField("Money", DBUsers.getValueString(author.getId(), "MONEY"), true)
						.setFooter(author.getName(), author.getEffectiveAvatarUrl());
				
				channel.sendMessage(embed.build()).queue();
			}
			catch (NullPointerException ignored) {
				DBUsers.registerUser(event.getAuthor().getId());
				channel.sendMessage("Oops! Try again?").queue();
			}
		}
		else {
			try {
				User user = event.getMessage().getMentionedUsers().get(0);
				Color roleColor = event.getGuild().retrieveMember(user).complete().getColor();
				EmbedBuilder embed = new EmbedBuilder()
						.setColor(roleColor)
						.setTitle(user.getName() + "'s Profile")
						.setThumbnail(user.getEffectiveAvatarUrl())
						.addField("Level", DBUsers.getValueString(user.getId(), "level"), false)
						.addField("Exp", DBUsers.getValueString(user.getId(), "exp"), true)
						.addField("Rep", DBUsers.getValueString(user.getId(), "rep"), true)
						.addField("Money", DBUsers.getValueString(user.getId(), "money"), true)
						.setFooter(user.getName(), user.getEffectiveAvatarUrl());
				
				channel.sendMessage(embed.build()).queue();
			}
			catch (NullPointerException ignored) {
				DBUsers.registerUser(event.getMessage().getMentionedUsers().get(0).getId());
				channel.sendMessage("Oops! Try again?").queue();
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
