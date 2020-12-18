package net.crimsonite.rena.commands.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

@CommandInfo(
		name = {"shardinfo"},
		description = "Shows the information about the current shard."
		)
public class ShardInfoCmd extends Command{
	
	private static int currentShard;
	private static int totalShard;
	
	public ShardInfoCmd() {
		this.name = "shardinfo";
		this.aliases = new String[] {"shard"};
		this.category = new Category("Informations");
		this.help = "Shows the information about the current shard.";
		this.guildOnly = true;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		currentShard = event.getJDA().getShardInfo().getShardId();
		totalShard = event.getJDA().getShardInfo().getShardTotal();
		
		event.replyFormatted("```" +
				"Current Shard: | %d\n" +
				"Total Shards:   | %d\n" +
				"```",
				currentShard, totalShard
				);
	}

}
