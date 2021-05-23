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

package net.crimsonite.rena.commands.info;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import com.sun.management.OperatingSystemMXBean;

import net.crimsonite.rena.RenaBot;
import net.crimsonite.rena.RenaConfig;
import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.engine.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StatusCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		JDA jda = event.getJDA();
		MessageChannel channel = event.getChannel();
		Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
		
		OperatingSystemMXBean operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		
		long numberOfCommands = HelpCommand.getCommandCount();
		long shards = jda.getShardInfo().getShardTotal();
		long timesCommandUsed = Command.getTimesCommandUsed();
		long threads = Thread.activeCount();
		long totalUptimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - RenaBot.startup);
		long days = totalUptimeInSeconds / 86400;
		long hours = (totalUptimeInSeconds % 86400) / 3600;
		long minutes = (totalUptimeInSeconds % 3600) / 60;
		double cpuLoad = operatingSystem.getCpuLoad();
		
		String timeFormat = "%dd, %dh, %dm".formatted(days, hours, minutes);
		
		if (days == 0 && hours == 0) {
			timeFormat = "%dm".formatted(minutes);
		}
		else if (days == 0) {
			timeFormat = "%dh, %dm".formatted(hours, minutes);
		}
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle(I18n.getMessage(event.getAuthor().getId(), "info.status.embed.title"), RenaConfig.GITHUB_URL)
				.addField(I18n.getMessage(event.getAuthor().getId(), "info.status.embed.version"), RenaConfig.VERSION_STRING, false)
				.addField(I18n.getMessage(event.getAuthor().getId(), "info.status.embed.uptime"), timeFormat, false)
				.addField(I18n.getMessage(event.getAuthor().getId(), "info.status.embed.number_of_commands"), String.valueOf(numberOfCommands), false)
				.addField(I18n.getMessage(event.getAuthor().getId(), "info.status.embed.times_command_used"), String.valueOf(timesCommandUsed), false)
				.addField(I18n.getMessage(event.getAuthor().getId(), "info.status.embed.guilds"), String.valueOf(jda.getGuilds().size()), false)
				.addField(I18n.getMessage(event.getAuthor().getId(), "info.status.embed.users"), String.valueOf(jda.getUsers().size()), false)
				.addField(I18n.getMessage(event.getAuthor().getId(), "info.status.embed.shards"), String.valueOf(shards), false);
		
		channel.sendMessage(embed.build()).queue();
		channel.sendMessageFormat(
				"```yml\n" +
				"**************************\n" +
				"** Internal Information **\n" +
				"**************************\n\n" +
				I18n.getMessage(event.getAuthor().getId(), "info.status.code_block.threads") +
				I18n.getMessage(event.getAuthor().getId(), "info.status.code_block.cpu_usage") +
				"```",
				
				threads,
				cpuLoad
				)
				.queue();
	}

	@Override
	public String getCommandName() {
		return "status";
	}
	
	@Override
	public String getCommandCategory() {
		return "Information";
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
