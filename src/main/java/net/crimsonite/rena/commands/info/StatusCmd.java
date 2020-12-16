package net.crimsonite.rena.commands.info;

import java.awt.Color;
import java.time.format.DateTimeFormatter;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

import net.crimsonite.rena.RenaInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

@CommandInfo(
		name = {"status"},
		description = "Shows the information about the bot."
		)
public class StatusCmd extends Command{
	
	public static User author;
	public static Color roleColor;
	
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	public StatusCmd() {
		this.name = "status";
		this.aliases = new String[] {"botinfo"};
		this.category = new Category("Informations");
		this.help = "Shows the information about the bot.";
		this.guildOnly = false;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		if (event.getAuthor().isBot())
			return;
		
		author = event.getAuthor();
		roleColor = event.getGuild().getMember(author).getColor();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(event.getSelfUser().getName() + "'s Informations", RenaInfo.GITHUB_URL)
				.addField("ID", event.getSelfUser().getId(), true)
				.addField("Date Created", event.getSelfUser().getTimeCreated().format(format), false)
				.addField("Version", RenaInfo.VERSION, true)
				.addField("Shard", event.getJDA().getShardInfo().getShardString(), true)
				.addField("Guilds", event.getJDA().getGuilds().size()+"", true)
				.addField("Users", event.getJDA().getUsers().size()+"", true)
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
		
		event.reply(embed.build());
		
	}

}
