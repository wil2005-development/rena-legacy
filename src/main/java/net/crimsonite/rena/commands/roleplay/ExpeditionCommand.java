package net.crimsonite.rena.commands.roleplay;

import java.awt.Color;
import java.util.Random;

import net.crimsonite.rena.database.DBUsers;
import net.crimsonite.rena.utils.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ExpeditionCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		User author = event.getAuthor();
		
		try {
			Random rng = new Random();
			Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
			int currentLevel = Integer.parseInt(DBUsers.getValueString(author.getId(), "level"));
			int baseReceivedMoney = rng.nextInt(10-1)+1;
			int baseReceivedExp = rng.nextInt(3-1)+1;
			int receivedMoney = baseReceivedMoney+currentLevel*2;
			int receivedExp = baseReceivedExp+currentLevel*2;
			
			DBUsers.incrementValue(author.getId(), "money", receivedMoney);
			DBUsers.incrementValue(author.getId(), "exp", receivedExp);
			
			EmbedBuilder embed = new EmbedBuilder()
					.setColor(roleColor)
					.setTitle("Rewards")
					.addField("Money", String.valueOf(receivedMoney), true)
					.addField("Exp", String.valueOf(receivedExp), true)
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			channel.sendMessage("**You went into an expedition to fulfill a commission...**").queue();
			channel.sendMessage(embed.build()).queue();
		}
		catch (NullPointerException ignored) {
			DBUsers.registerUser(author.getId());
			channel.sendMessage("Oops! Try again?").queue();
		}
	}

	@Override
	public String getCommandName() {
		return "expedition";
	}

	@Override
	public long cooldown() {
		return 64800;
	}

	@Override
	public boolean isOwnerCommand() {
		return false;
	}

}