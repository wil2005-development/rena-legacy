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
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UserinfoCommand extends Command {

    private static void sendEmbed(GuildMessageReceivedEvent event, Member member) {
        User memberAsUser = member.getUser();
        Member author = event.getMember();
        Color roleColor = event.getGuild().retrieveMember(memberAsUser).complete().getColor();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");

        if (author == null) throw new NullPointerException();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(roleColor)
                .setTitle(I18n.getMessage(event.getAuthor().getId(), "info.user_info.embed.title").formatted(member.getEffectiveName()))
                .setThumbnail(memberAsUser.getEffectiveAvatarUrl())
                .addField(I18n.getMessage(event.getAuthor().getId(), "info.user_info.embed.user_id"), member.getId(), false)
                .addField(I18n.getMessage(event.getAuthor().getId(), "info.user_info.embed.date_created"), member.getTimeCreated().format(format), false)
                .addField(I18n.getMessage(event.getAuthor().getId(), "info.user_info.embed.date_joined"), member.getTimeJoined().format(format), false)
                .setFooter(author.getEffectiveName(), author.getUser().getEffectiveAvatarUrl());

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        MessageChannel channel = event.getChannel();
        Member author = event.getMember();
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

        if (author == null) throw new NullPointerException();

        if (args.length == 1) {
            sendEmbed(event, author);
        } else if (args.length >= 2) {
            if (!(mentionedMembers.isEmpty() || mentionedMembers.get(0).getUser().isBot())) {
                Member member = event.getMessage().getMentionedMembers().get(0);
                sendEmbed(event, member);
            } else {
                List<Member> listedMembers = FinderUtil.findMembers(args[1], event.getGuild());

                if (listedMembers.isEmpty() || listedMembers.get(0).getUser().isBot()) {
                    channel.sendMessage(I18n.getMessage(event.getAuthor().getId(), "info.user_info.user_not_found")).queue();
                    event.getGuild().loadMembers();
                } else {
                    Member member = listedMembers.get(0);
                    sendEmbed(event, member);
                }
            }
        }
    }

    @Override
    public String getCommandName() {
        return "userinfo";
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
