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

package net.crimsonite.rena.commands.games;

import java.awt.Color;
import java.util.Random;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.Cooldown;
import net.crimsonite.rena.core.GameHandler;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LootCommand extends Command {

    private boolean shouldRemoveCooldown = false;
    private String playerId;

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        User author = event.getAuthor();
        MessageChannel channel = event.getChannel();

        try {
            Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
            Random rng = new Random();

            int currentLevel = DBReadWrite.getValueInt(Table.PLAYERS, author.getId(), "LEVEL");
            int baseReceivedExp = rng.nextInt(3 - 1) + 1;
            int baseReceivedMoney = rng.nextInt(10 - 1) + 1;
            int receivedExp = baseReceivedExp + currentLevel * 2;
            int receivedMoney = baseReceivedMoney + currentLevel * 2;

            DBReadWrite.incrementValue(Table.PLAYERS, author.getId(), "MONEY", receivedMoney);
            DBReadWrite.incrementValue(Table.PLAYERS, author.getId(), "EXP", receivedExp);
            GameHandler.Handler.handleLevelup(author.getId());

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(roleColor)
                    .setTitle(I18n.getMessage(author.getId(), "game.loot.embed.title"))
                    .addField(I18n.getMessage(author.getId(), "game.loot.embed.money"), String.valueOf(receivedMoney), true)
                    .addField(I18n.getMessage(author.getId(), "game.loot.embed.exp"), String.valueOf(receivedExp), true)
                    .setFooter(author.getName(), author.getEffectiveAvatarUrl());

            channel.sendMessage(I18n.getMessage(author.getId(), "game.loot.dialogue")).queue();
            channel.sendMessageEmbeds(embed.build()).queue();
            channel.sendMessage(I18n.getMessage(author.getId(), "game.loot.no_item")).queue();
        } catch (NullPointerException ignored) {
            DBReadWrite.registerUser(author.getId());

            channel.sendMessage(I18n.getMessage(author.getId(), "common_string.late_registration")).queue();

            this.playerId = author.getId();
            this.shouldRemoveCooldown = true;
        }
    }

    @Override
    public void postCommandEvent() {
        if (this.shouldRemoveCooldown) {
            Cooldown.removeCooldown(this.playerId, getCommandName());
        }
    }

    @Override
    public String getCommandName() {
        return "loot";
    }

    @Override
    public String getCommandCategory() {
        return "Games";
    }

    @Override
    public long cooldown() {
        return 43_200;
    }

    @Override
    public boolean isOwnerCommand() {
        return false;
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
