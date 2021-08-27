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

package net.crimsonite.rena.commands.dev;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.core.database.DBReadWrite;
import net.crimsonite.rena.core.database.DBReadWrite.Table;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ModifyAttributesCommand extends Command {

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        MessageChannel channel = event.getChannel();
        String message = "```diff\n+SUCCESS: [%s] Operation executed successfully!```";

        Table table;

        try {
            switch (args[5].toUpperCase()) {
                case "USERS" -> table = Table.USERS;
                case "PLAYERS" -> table = Table.PLAYERS;
                case "GUILDS" -> table = Table.GUILDS;
                default -> {
                    channel.sendMessage("```diff\n-ERROR: Table not specified.```").queue();

                    return;
                }
            }

            switch (args[1].toUpperCase()) {
                case "BOOLEAN" -> {
                    DBReadWrite.modifyDataBoolean(table, args[2], args[3], Boolean.parseBoolean(args[4]));
                    channel.sendMessageFormat(message, args[1]).queue();
                }
                case "INT" -> {
                    DBReadWrite.modifyDataInt(table, args[2], args[3], Integer.parseInt(args[4]));
                    channel.sendMessageFormat(message, args[1]).queue();
                }
                case "INT_INCREMENT" -> {
                    DBReadWrite.incrementValue(table, args[2], args[3], Integer.parseInt(args[4]));
                    channel.sendMessageFormat(message, args[1]).queue();
                }
                case "INT_DECREMENT" -> {
                    DBReadWrite.decrementValue(table, args[2], args[3], Integer.parseInt(args[4]));
                    channel.sendMessageFormat(message, args[1]).queue();
                }
                case "STRING" -> {
                    DBReadWrite.modifyDataString(table, args[2], args[3], args[4]);
                    channel.sendMessageFormat(message, args[1]).queue();
                }
                case "MAP_INCREMENT" -> {
                    String[] ctx = args[3].split("\\.");

                    if (ctx.length == 2) {
                        DBReadWrite.incrementValueFromMap(table, args[2], ctx[0], ctx[1], Integer.parseInt(args[4]));
                        channel.sendMessageFormat(message, args[1]).queue();
                    } else {
                        channel.sendMessage("```diff\n-ERROR: Arguments not satisfied```").queue();
                    }

                }
                case "MAP_DECREMENT" -> {
                    String[] ctx = args[3].split("\\.");

                    if (ctx.length == 2) {
                        DBReadWrite.decrementValueFromMap(table, args[2], ctx[0], ctx[1], Integer.parseInt(args[4]));
                        channel.sendMessageFormat(message, args[1]).queue();

                    } else {
                        channel.sendMessage("```diff\n-ERROR: Arguments not satisfied```").queue();
                    }
                }
                default -> channel.sendMessage("```diff\n-ERROR: Invalid Argument```").queue();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            channel.sendMessage("```diff\n-ERROR: Received no Arguments```").queue();
        } catch (IllegalArgumentException e) {
            channel.sendMessage("```diff\n-ERROR: Received an Illegal Argument```").queue();
        } catch (NullPointerException e) {
            channel.sendMessage("```diff\n-ERROR: Operation returned a null value```").queue();
        }
    }

    @Override
    public String getCommandName() {
        return "modify";
    }

    @Override
    public String getCommandCategory() {
        return "Dev";
    }

    @Override
    public long cooldown() {
        return 0;
    }

    @Override
    public boolean isOwnerCommand() {
        return true;
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
