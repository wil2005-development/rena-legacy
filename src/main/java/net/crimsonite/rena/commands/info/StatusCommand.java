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
import java.lang.management.MemoryMXBean;

import com.sun.management.OperatingSystemMXBean;

import net.crimsonite.rena.RenaInfo;
import net.crimsonite.rena.commands.Command;
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
		
		OperatingSystemMXBean operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
		
		Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
		
		EmbedBuilder embed = new EmbedBuilder()
				.setColor(roleColor)
				.setTitle("Rena's Informations", RenaInfo.GITHUB_URL)
				.addField("Version", RenaInfo.VERSION_STRING, false)
				.addField("Number of Commands", String.valueOf(HelpCommand.getCommandCount()), false)
				.addField("Times Command Used", String.valueOf(Command.getTimesCommandUsed()), false)
				.addField("Guilds", String.valueOf(jda.getGuilds().size()), false)
				.addField("Users", String.valueOf(jda.getUsers().size()), false);

		channel.sendMessage(embed.build()).queue();
		channel.sendMessageFormat(
				"```yml\n" +
				"**************************\n" +
				"** Internal Information **\n" +
				"**************************\n\n" +
				"Shards: %d\n" +
				"Threads : %d\n" +
				"** Debug **\n" +
				"CPU Usage: %.2f%%\n" +
				"Total Memory: %dmb\n" +
				"Used memory: %dmb\n" +
				"Available Memory: %dmb\n" +
				"Max Heap Memory: %dmb\n" +
				"Max Non-Heap Memory: %dmb\n" +
				"```",
				
				// TODO Cleanup and move system informations to developer's command
				jda.getShardInfo().getShardTotal(),
				Thread.activeCount(),
				operatingSystem.getCpuLoad(),
				operatingSystem.getTotalMemorySize() / (1024 * 1024),
				((operatingSystem.getTotalMemorySize() / (1024 * 1024)) - (operatingSystem.getFreeMemorySize() / (1024 * 1024))),
				operatingSystem.getFreeMemorySize() / (1024 * 1024),
				memory.getHeapMemoryUsage().getMax() / (1024 * 1024),
				memory.getNonHeapMemoryUsage().getMax() / (1024 * 1024))
				.queue();
	}

	@Override
	public String getCommandName() {
		return "status";
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
