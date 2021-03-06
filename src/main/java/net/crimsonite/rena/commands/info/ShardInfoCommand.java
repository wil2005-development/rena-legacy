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

import net.crimsonite.rena.RenaConfig;
import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.ShardInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

public class ShardInfoCommand extends Command {

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        User author = event.getAuthor();
        Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
        JDA jda = event.getJDA();
        MessageChannel channel = event.getChannel();
        ShardInfo shardInfo = jda.getShardInfo();
        ShardManager shardManager = jda.getShardManager();

        long totalUsers = 0;
        long totalGuilds = 0;
        long shardUsers = 0;
        long shardGuilds = 0;

        for (Guild guild : (shardManager != null ? shardManager.getGuilds() : jda.getGuilds())) {
            for (Member member : guild.getMembers()) {
                if (!member.getUser().isBot()) {
                    totalUsers++;
                }
            }

            totalGuilds++;
        }

        for (Guild guild : jda.getGuilds()) {
            for (Member member : guild.getMembers()) {
                if (!member.getUser().isBot()) {
                    shardUsers++;
                }
            }

            shardGuilds++;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(roleColor)
                .setTitle(I18n.getMessage(author.getId(), "info.shard_info.embed.title").formatted(jda.getShardInfo().getShardString()))
                .addField(I18n.getMessage(author.getId(), "info.shard_info.embed.shard_id"), "#" + shardInfo.getShardId(), false)
                .setFooter(author.getName(), author.getEffectiveAvatarUrl());

        if (RenaConfig.isSharding() && (shardManager != null)) {
            embed.addField(I18n.getMessage(author.getId(), "info.shard_info.embed.average_gateway_ping"), "%dm/s".formatted(Math.round(shardManager.getAverageGatewayPing())), false)
                    .addBlankField(false)
                    .addField(I18n.getMessage(author.getId(), "info.shard_info.embed.total_shards"), String.valueOf(shardManager.getShardsTotal()), true)
                    .addField(I18n.getMessage(author.getId(), "info.shard_info.embed.online_shards"), String.valueOf(shardManager.getShardsRunning()), true)
                    .addBlankField(false)
                    .addField(I18n.getMessage(author.getId(), "info.shard_info.embed.total_guilds"), String.valueOf(totalGuilds), true)
                    .addField(I18n.getMessage(author.getId(), "info.shard_info.embed.shard_guilds"), String.valueOf(shardGuilds), true)
                    .addBlankField(false)
                    .addField(I18n.getMessage(author.getId(), "info.shard_info.embed.total_users"), String.valueOf(totalUsers), true)
                    .addField(I18n.getMessage(author.getId(), "info.shard_info.embed.shard_users"), String.valueOf(shardUsers), true);
        }
        else {
            embed.addBlankField(false)
                    .addField(I18n.getMessage(author.getId(), "info.shard_info.embed.total_guilds"), String.valueOf(totalGuilds), true)
                    .addField(I18n.getMessage(author.getId(), "info.shard_info.embed.total_users"), String.valueOf(totalUsers), true);
        }

        channel.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public String getCommandName() {
        return "shardinfo";
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

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

}
