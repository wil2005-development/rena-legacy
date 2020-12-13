package net.crimsonite.rena.commands.info;

import java.awt.Color;
import java.time.format.DateTimeFormatter;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

@CommandInfo(
		name = {"profile"},
		description = "Shows the author's profile/member's if specified."
		)
public class UserInfoCmd extends Command{
	
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	public UserInfoCmd() {
		this.name = "profile";
		this.aliases = new String[] {"iam", "userinfo"};
		this.category = new Category("Informations");
		this.help = "Shows the author's profile/member's if specified.";
		this.arguments = "[mention]";
		this.guildOnly = true;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		if (event.getAuthor().isBot())
			return;
		
		if (event.getArgs().isEmpty()) {
			User author = event.getAuthor();
			
			EmbedBuilder embed = new EmbedBuilder()
					.setColor(Color.CYAN)
					.setTitle(author.getName() + "'s Profile")
					.setThumbnail(author.getEffectiveAvatarUrl())
					.addField("ID", author.getId(), false)
					.addField("Date Created", author.getTimeCreated().format(format), false)
					.setFooter(author.getName(), author.getEffectiveAvatarUrl());
			
			event.reply(embed.build());
		}
		else {
			User author = event.getAuthor();
			
			try {
				User user = event.getMessage().getMentionedUsers().get(0);
				
				EmbedBuilder embed = new EmbedBuilder()
						.setColor(Color.CYAN)
						.setTitle(user.getName() + "'s Profile")
						.setThumbnail(user.getEffectiveAvatarUrl())
						.addField("ID", user.getId(), false)
						.addField("Date Created", user.getTimeCreated().format(format), false)
						.setFooter(author.getName(), author.getEffectiveAvatarUrl());
				
				event.reply(embed.build());
			}
			catch (IndexOutOfBoundsException e) {
				event.reply("Please mention a user.");
			}
		}
		
	}
	
}
