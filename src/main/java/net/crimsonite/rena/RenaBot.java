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

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.crimsonite.rena.commands.dev.ModifyAttributesCommand;
import net.crimsonite.rena.commands.dev.ShutdownCommand;
import net.crimsonite.rena.commands.dev.StatusReportCommand;
import net.crimsonite.rena.commands.games.DailyCommand;
import net.crimsonite.rena.commands.games.ExpeditionCommand;
import net.crimsonite.rena.commands.games.HuntCommand;
import net.crimsonite.rena.commands.games.InsightCommand;
import net.crimsonite.rena.commands.games.InventoryCommand;
import net.crimsonite.rena.commands.games.LootCommand;
import net.crimsonite.rena.commands.games.ProfileCommand;
import net.crimsonite.rena.commands.games.RepCommand;
import net.crimsonite.rena.commands.games.TransferMoneyCommand;
import net.crimsonite.rena.commands.games.UseItemCommand;
import net.crimsonite.rena.commands.imageboard.DanbooruCommand;
import net.crimsonite.rena.commands.imageboard.SafebooruCommand;
import net.crimsonite.rena.commands.info.AvatarCommand;
import net.crimsonite.rena.commands.info.GuildinfoCommand;
import net.crimsonite.rena.commands.info.HelpCommand;
import net.crimsonite.rena.commands.info.PingCommand;
import net.crimsonite.rena.commands.info.RoleinfoCommand;
import net.crimsonite.rena.commands.info.ShardInfoCommand;
import net.crimsonite.rena.commands.info.StatusCommand;
import net.crimsonite.rena.commands.info.UserinfoCommand;
import net.crimsonite.rena.commands.misc.DiceCommand;
import net.crimsonite.rena.commands.misc.EightBallCommand;
import net.crimsonite.rena.commands.moderation.BanCommand;
import net.crimsonite.rena.commands.moderation.KickCommand;
import net.crimsonite.rena.commands.moderation.SetGuildPrefixCommand;
import net.crimsonite.rena.commands.moderation.UnbanCommand;
import net.crimsonite.rena.commands.userpreference.PreferenceCommand;
import net.crimsonite.rena.core.CommandRegistry;
import net.crimsonite.rena.core.database.DBConnection;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class RenaBot extends CommandRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RenaBot.class);

    private static boolean dbIsActive;

    private static final long startupTime = System.currentTimeMillis();

    protected RenaBot() {
        logger.info("Preparing bot for activation...");

        try {
            DefaultShardManagerBuilder jdaBuilder = DefaultShardManagerBuilder.createDefault(RenaConfig.TOKEN)
                    .setStatus(OnlineStatus.ONLINE)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL);

            if (dbIsActive) {
                jdaBuilder.addEventListeners(
                        // Roleplaying Commands
                        registerCommand(new ExpeditionCommand()),
                        registerCommand(new InsightCommand()),
                        registerCommand(new InventoryCommand()),
                        registerCommand(new UseItemCommand()),
                        registerCommand(new DailyCommand()),
                        registerCommand(new HuntCommand()),
                        registerCommand(new LootCommand()),
                        registerCommand(new ProfileCommand()),
                        registerCommand(new RepCommand()),
                        registerCommand(new TransferMoneyCommand()),

                        // User Preference Commands
                        registerCommand(new PreferenceCommand()),

                        // Guild Preference Commands
                        registerCommand(new SetGuildPrefixCommand())
                );
            }

            jdaBuilder.addEventListeners(
                    // Info Commands
                    registerCommand(new AvatarCommand()),
                    registerCommand(new UserinfoCommand()),
                    registerCommand(new GuildinfoCommand()),
                    registerCommand(new HelpCommand()),
                    registerCommand(new PingCommand()),
                    registerCommand(new RoleinfoCommand()),
                    registerCommand(new ShardInfoCommand()),
                    registerCommand(new StatusCommand()),

                    // Moderation Commands
                    registerCommand(new UnbanCommand()),
                    registerCommand(new BanCommand()),
                    registerCommand(new KickCommand()),

                    // Miscellaneous Commands
                    registerCommand(new EightBallCommand()),
                    registerCommand(new DiceCommand()),

                    // Imageboard Commands
                    registerCommand(new DanbooruCommand()),
                    registerCommand(new SafebooruCommand()),

                    // Developer/Debug Command
                    new ModifyAttributesCommand(),
                    new ShutdownCommand(),
                    new StatusReportCommand(),

                    // Event Listener
                    new ReadyListener()
            );

            if (RenaConfig.isSharding()) {
                int totalShards = RenaConfig.getTotalShards();

                logger.info("Loading (%d) shards...".formatted(totalShards));

                List<Integer> shardIds = new ArrayList<>();

                for (int i = 0; i < totalShards; i++) {
                    shardIds.add(i);
                }

                jdaBuilder.setShardsTotal(totalShards)
                        .setShards(shardIds);
            }

            jdaBuilder.build();
        } catch (NullPointerException e) {
            logger.error("A config variable returned a null value.");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            logger.error("Failed to login, try checking if the Token and config variables are provided correctly.");
        } catch (LoginException e) {
            logger.error("Failed to login, try checking if the provided Token is valid.");
        }
    }

    public static void main(String[] args) {
        logger.info("Starting up...");

        try {
            DBConnection.conn();
            dbIsActive = true;
        } catch (Exception ignored) {
            logger.warn("Couldn't connect to database. Some commands might fail, but the bot will remain active.");
            dbIsActive = false;
        }

        new RenaBot();
    }

    /**
     * Gives the time when the bot was executed.
     *
     * @return time since the bot was executed.
     */
    public static long getStartupTime() {
        return startupTime;
    }

}
