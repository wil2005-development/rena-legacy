package net.crimsonite.rena.commands.info;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.crimsonite.rena.utils.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RoleinfoCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		if (args.length == 1) {
			channel.sendMessage("*You want me to look for nothing?!*").queue();
		}
		else if (args.length >= 2) {
			List<Role> listedRoles = FinderUtil.findRoles(args[1], event.getGuild());
			
			if (listedRoles.isEmpty()) {
				channel.sendMessage("*There's no such role*").queue();
			}
			else {
				Role role = listedRoles.get(0);
				User author = event.getAuthor();
				DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
				EmbedBuilder embed = new EmbedBuilder()
						.setColor(role.getColor())
						.setTitle("Informations for role: " + role.getName())
						.addField("ID", role.getId(), false)
						.addField("Date Created: ", role.getTimeCreated().format(format), false)
						.addField("Color", "#"+Integer.toHexString(role.getColorRaw()).toUpperCase(), false)
						.addField("Guild", role.getGuild().getName(), false)
						.addField("Permissions", role.getPermissions().toString(), false)
						.setFooter(author.getName(), author.getEffectiveAvatarUrl());
				
				channel.sendMessage(embed.build()).queue();
			}
		}
	}

	@Override
	public String getCommandName() {
		return "roleinfo";
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
