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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.I18n;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class InventoryCommand extends Command {

    User author;

    private static String replaceItemIdWithName(User author, String str) {
        str = str.replace("ITEM_0X194", "ITEM_OX194")
                .replace("SEED_OF_LIFE", I18n.getMessage(author.getId(), "game.inventory.items.seed_of_life"))
                .replace("SEED_OF_WISDOM", I18n.getMessage(author.getId(), "game.inventory.items.seed_of_wisdom"))
                .replace("ELIXIR_OF_LIFE", I18n.getMessage(author.getId(), "game.inventory.items.elixir_of_life"))
                .replace("ELIXIR_OF_MANA", I18n.getMessage(author.getId(), "game.inventory.items.elixir_of_mana"));

        return str;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        this.author = event.getAuthor();
        Color roleColor = event.getGuild().retrieveMember(author).complete().getColor();
        MessageChannel channel = event.getChannel();

        StringBuilder itemField = new StringBuilder();
        InputStream icon = getClass().getClassLoader().getResourceAsStream("assets/icons/inventory_icon.png");

        if (icon == null) throw new NullPointerException();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(roleColor)
                .setTitle(I18n.getMessage(author.getId(), "game.inventory.embed.title"))
                .setThumbnail("attachment://inventory_icon.png")
                .setFooter(author.getName(), author.getEffectiveAvatarUrl());

        Map<String, Long> itemList = DBReadWrite.getValueMapSL(Table.PLAYERS, author.getId(), "INVENTORY");
        Map<String, Integer> inventory = new HashMap<>();
        List<String> itemListKeys = new ArrayList<>(itemList.keySet());

        for (String item : itemListKeys) {
            if (!(itemList.get(item) == 0)) {
                inventory.put(item, itemList.get(item).intValue());
            }
        }

        List<String> inventoryKeys = new ArrayList<>(inventory.keySet());

        for (String item : inventoryKeys) {
            int amount = inventory.get(item);
            itemField.append("`%1$s: %2$s`, ".formatted(item, amount));
        }

        String currentItems = replaceItemIdWithName(author, itemField.toString());

        embed.addField(I18n.getMessage(author.getId(), "game.inventory.embed.items"), currentItems.substring(0, (currentItems.length() - 2)), false);

        channel.sendMessageEmbeds(embed.build()).addFile(icon, "inventory_icon.png").queue();
    }

    @Override
    public String getCommandName() {
        return "inventory";
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
        return I18n.getMessage(author.getId(), "help.games.inventory.description");
    }

    @Override
    public String getUsage() {
        return I18n.getMessage(author.getId(), "help.games.inventory.usage");
    }

}
