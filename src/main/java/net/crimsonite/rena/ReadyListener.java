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

package net.crimsonite.rena;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.crimsonite.rena.utils.RandomGenerator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class ReadyListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ReadyListener.class);

    @Override
    public void onReady(ReadyEvent event) {
        int shardId = event.getJDA().getShardInfo().getShardId();
        String quote = "Engine has started! [Shard #%d]";
        JDA jda = event.getJDA();
        LocalDate date = LocalDate.now();

        CommandListUpdateAction slashCommands = jda.updateCommands();
        slashCommands.addCommands(new CommandData("help", "Shows a help text.")).queue();

        if (date.getMonth() == Month.JANUARY || date.getDayOfMonth() == 1) {
            quote = "Happy New Year! [Shard #%d]";
        } else if (date.getMonth() == Month.DECEMBER || date.getDayOfMonth() == 25) {
            quote = "Merry Christmas! [Shard #%d]";
        } else {
            InputStream inputStream;
            BufferedReader reader;
            String line;

            List<String> listOfQuotes = new ArrayList<>();

            try {
                inputStream = getClass().getClassLoader().getResourceAsStream("assets/status_quotes.txt");

                if (inputStream == null) throw new NullPointerException();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {
                    listOfQuotes.add(line);
                }

                quote = listOfQuotes.get(RandomGenerator.randomInt(listOfQuotes.size()));
            } catch (IOException e) {
                logger.warn("Shard #%d failed to set activity. Using default activity instead...");
                e.printStackTrace();
            } catch (NullPointerException e) {
                logger.warn("Can't read or find \"status_quotes.txt\"");
                e.printStackTrace();
            }
        }

        event.getJDA().getPresence().setActivity(Activity.playing(quote.formatted(shardId)));

        logger.info("Shard #%1$d activated in %2$d second(s).".formatted(shardId, ((System.currentTimeMillis() - RenaBot.getStartupTime()) / 1000)));
    }

}
