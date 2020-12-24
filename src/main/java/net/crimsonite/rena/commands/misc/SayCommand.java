package net.crimsonite.rena.commands.misc;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;

@CommandInfo(
		name = {"say"},
		description = "Repeats the arguments passed after the command."
		)
public class SayCommand extends Command{
	
	public SayCommand() {
		this.name = "say";
		this.aliases = new String[] {"repeatafterme"};
		this.category = new Category("Miscellaneous");
		this.help = "Repeats the arguments passed after the command.";
		this.arguments = "[message]";
		this.guildOnly = true;
		this.cooldown = 5;
	}

	@Override
	protected void execute(CommandEvent event) {
		if (event.getAuthor().isBot())
			return;
		
		if (event.getArgs().isEmpty()) {
			event.reply("Want me to repeat after nothing?!");
		}
		else {
			event.reply(event.getArgs());
		}
		
	}
}
