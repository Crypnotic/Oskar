package me.crypnotic.oskar.managers;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

import de.btobastian.javacord.entities.message.Message;
import me.crypnotic.oskar.objects.ICommand;
import me.crypnotic.oskar.objects.commands.KickCommand;
import me.crypnotic.oskar.objects.commands.PurgeCommand;
import me.crypnotic.oskar.utilities.Multisets;

public class CommandManager {

	private Map<String, ICommand> commands;

	public CommandManager() {
		this.commands = new HashMap<String, ICommand>();
	}

	public void init() {
		commands.put("kick", new KickCommand());
		commands.put("purge", new PurgeCommand());
	}

	public void handle(Message message, String[] data) {
		Optional<ICommand> result = getCommand(data[0].toLowerCase());
		if (result.isPresent()) {
			result.get().execute(message, Multisets.transform(data, 1), System.currentTimeMillis());
		} else {
			message.reply("Unknown command : " + data[0]);
		}
	}

	public Optional<ICommand> getCommand(String name) {
		ICommand command = commands.get(name);
		if (command != null) {
			return Optional.of(command);
		}
		return Optional.absent();
	}
}
