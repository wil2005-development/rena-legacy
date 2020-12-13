package net.crimsonite.rena.commands.info;

import java.awt.Color;
import java.time.format.DateTimeFormatter;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@CommandInfo(
		name = {"guildinfo"},
		description = "Shows the informations about the guild."
		)
public class GuildInfoCmd extends Command{
	
	private static DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	public GuildInfoCmd() {
		this.name = "guild";
		this.aliases = new String[] {"guildinfo, serverinfo"};
		this.category = new Category("Informations");
		this.help = "Shows the informations about the guild.";
		this.guildOnly = false;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		Guild guild = event.getGuild();
		User author = event.getAuthor();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(Color.RED)
				.setTitle("Showing informations for " + guild.getName())
				.setThumbnail(guild.getIconUrl())
				.addField("ID", guild.getId(), false)
				.addField("Date Created", guild.getTimeCreated().format(format), false)
				.addField("Owner", guild.getOwner().getEffectiveName(), false)
				.addField("Members", "" + guild.getMemberCount(), true)
				.addField("Roles", "" + guild.getRoles().size(), true)
				.setFooter(author.getName(), author.getEffectiveAvatarUrl());
				
		event.reply(embed.build());
	}

}
