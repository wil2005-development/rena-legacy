package net.crimsonite.rena.commands.games;

import java.awt.Color;
import java.util.List;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.GameHandler.Handler;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class InsightCommand extends Command {

    User author;

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        this.author = event.getAuthor();
        MessageChannel channel = event.getChannel();

        Color roleColor;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(I18n.getMessage(author.getId(), "game.insight.embed.title"))
                .setFooter(author.getName(), author.getEffectiveAvatarUrl());

        if (args.length >= 2) {
            switch (args[1]) {
                case "exp" -> {
                    int currentExp;
                    int requiredExpForNextLevel;
                    int expNeededForNextLevel;
                    if (args.length >= 3) {
                        if (!event.getMessage().getMentionedMembers().isEmpty()) {
                            Member member = event.getMessage().getMentionedMembers().get(0);

                            try {
                                currentExp = DBReadWrite.getValueInt(Table.PLAYERS, member.getId(), "EXP");
                                requiredExpForNextLevel = Handler.getRequiredExpForNextLevel(member.getId());
                                expNeededForNextLevel = (requiredExpForNextLevel - currentExp);
                                roleColor = event.getGuild().retrieveMember(member.getUser()).complete().getColor();
                                embed.setThumbnail(member.getUser().getEffectiveAvatarUrl());
                            } catch (NullPointerException e) {
                                DBReadWrite.registerUser(member.getId());
                                channel.sendMessage(I18n.getMessage(author.getId(), "common_string.late_registration")).queue();

                                return;
                            }
                        } else {
                            List<Member> listedMembers = FinderUtil.findMembers(args[2], event.getGuild());

                            if (listedMembers.isEmpty()) {
                                channel.sendMessage(I18n.getMessage(author.getId(), "common_string.late_registration")).queue();
                                event.getGuild().loadMembers();

                                return;
                            } else {
                                try {
                                    Member member = listedMembers.get(0);

                                    currentExp = DBReadWrite.getValueInt(Table.PLAYERS, member.getId(), "EXP");
                                    requiredExpForNextLevel = Handler.getRequiredExpForNextLevel(member.getId());
                                    expNeededForNextLevel = (requiredExpForNextLevel - currentExp);
                                    roleColor = event.getGuild().retrieveMember(member.getUser()).complete().getColor();
                                    embed.setThumbnail(member.getUser().getEffectiveAvatarUrl());
                                } catch (NullPointerException e) {
                                    DBReadWrite.registerUser(listedMembers.get(0).getId());
                                    channel.sendMessage(I18n.getMessage(author.getId(), "common_string.late_registration")).queue();

                                    return;
                                }
                            }
                        }
                    } else {
                        currentExp = DBReadWrite.getValueInt(Table.PLAYERS, author.getId(), "EXP");
                        requiredExpForNextLevel = Handler.getRequiredExpForNextLevel(author.getId());
                        expNeededForNextLevel = (requiredExpForNextLevel - currentExp);
                        roleColor = event.getGuild().retrieveMember(author).complete().getColor();
                        embed.setThumbnail(author.getEffectiveAvatarUrl());
                    }

                    String fieldName = I18n.getMessage(author.getId(), "game.insight.embed.next_exp");
                    String fieldValue = I18n.getMessage(author.getId(), "game.insight.embed.next_exp_value").formatted(requiredExpForNextLevel, expNeededForNextLevel);
                    embed.setColor(roleColor)
                            .addField(fieldName, fieldValue, false);
                }
                default -> {
                    channel.sendMessage(I18n.getMessage(author.getId(), "game.insight.cannot_predict")).queue();

                    return;
                }
            }

            channel.sendMessageEmbeds(embed.build()).queue();
        } else {
            channel.sendMessage(I18n.getMessage(author.getId(), "game.insight.nothing_to_predict")).queue();
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

    @Override
    public String getHelp() {
        return I18n.getMessage(author.getId(), "help.games.insight.description");
    }

    @Override
    public String getUsage() {
        return I18n.getMessage(author.getId(), "help.games.insight.usage");
    }

}
