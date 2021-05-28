package net.crimsonite.rena.commands.games;

import java.awt.Color;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.PlayerManager.Handler;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InsightCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
		MessageChannel channel = event.getChannel();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(I18n.getMessage(author.getId(), "roleplay.insight.embed.title"))
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
		
		if (args.length >= 2) {
			boolean flag = false;
			
			switch (args[1]) {
				case "exp":
					int currentExp = DBReadWrite.getValueInt(Table.PLAYERS, author.getId(), "EXP");
					int requiredExpForNextLevel = Handler.getRequiredExpForNextLevel(author.getId());
					int expNeededForNextLevel = (requiredExpForNextLevel - currentExp);
					
					String fieldName = I18n.getMessage(author.getId(), "roleplay.insight.embed.next_exp");
					String fieldValue = I18n.getMessage(author.getId(), "roleplay.insight.embed.next_exp_value").formatted(requiredExpForNextLevel, expNeededForNextLevel);
					
					embed.addField(fieldName, fieldValue, false);
					flag = true;
					
					break;
				default:
					channel.sendMessage(I18n.getMessage(author.getId(), "roleplay.insight.cannot_predict")).queue();
					
					break;
			}
			
			if (flag) {
				channel.sendMessage(embed.build()).queue();
			}
		}
		else {
			channel.sendMessage(I18n.getMessage(author.getId(), "roleplay.insight.nothing_to_predict")).queue();
		}
	}

	@Override
	public String getCommandName() {
		return "insight";
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
		return 5;
	}

}
