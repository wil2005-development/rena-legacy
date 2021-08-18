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

package net.crimsonite.rena.core;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.crimsonite.rena.commands.Command;

public abstract class CommandRegistry {

    private static final Logger logger = LoggerFactory.getLogger(CommandRegistry.class);
    private static int commandCount = 0;
    private static HashMap<String, Command> commands;

    public CommandRegistry() {
        commands = new HashMap<>();
    }

    /**
     * Gives a map of the currently registered commands.
     *
     * @return a hashmap of registered commands.
     */
    public static HashMap<String, Command> getRegisteredCommands() {
        return commands;
    }

    /**
     * Gives the number of the currently registered commands.
     *
     * @return the number of registered commands.
     */
    public static int getRegisteredCommandCount() {
        return commandCount;
    }

    /**
     * Registers and returns the command passed.
     *
     * @param command The Command to be registered.
     * @return the command passed.
     */
    public static Command registerCommand(Command command) {
        String commandName = command.getCommandName();
        commands.put(commandName, command);
        commandCount++;

        logger.info("\"%1$s\" command loaded.".formatted(commandName));

        return command;
    }

}
