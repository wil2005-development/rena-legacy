package net.crimsonite.rena.commands.dev;

import net.crimsonite.rena.commands.Command;
import net.crimsonite.rena.database.DBUsers;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ModifyAttributesCommand extends Command {

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		String message = "```diff\n+SUCCESS: [%s]Operation executed successfully!```";
		
		try {
			// For some reason, the switch-case 
			if (args[1] == "BOOLEAN") {
				DBUsers.modifyDataBoolean(args[2], args[3], Boolean.parseBoolean(args[4]));
				channel.sendMessageFormat(message, args[1]).queue();
			}
			else if (args[1] == "INT") {
				DBUsers.modifyDataInt(args[2], args[3], Integer.parseInt(args[4]));
				channel.sendMessageFormat(message, args[1]).queue();
			}
			else if (args[1] == "STRING") {
				DBUsers.modifyDataString(args[2], args[3], args[4]);
				channel.sendMessageFormat(message, args[1]).queue();
			}
			else {
				channel.sendMessage("```diff\n-ERROR: Invalid Argument```").queue();
			}
		}
		catch (ArrayIndexOutOfBoundsException ignored) {
			channel.sendMessage("```diff\n-ERROR: Received no Arguments```").queue();
		}
		catch (IllegalArgumentException ignored) {
			channel.sendMessage("```diff\n-ERROR: Received an Illegal Argument```").queue();
		}
		catch (NullPointerException ignored) {
			channel.sendMessage("```diff\n-ERROR: Operation returned a null value```").queue();
		}
	}

	@Override
	public String getCommandName() {
		return "modify";
	}

	@Override
	public long cooldown() {
		return 0;
	}

	@Override
	public boolean isOwnerCommand() {
		return true;
	}

}
