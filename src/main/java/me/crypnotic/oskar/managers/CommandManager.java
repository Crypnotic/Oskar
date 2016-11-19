package me.crypnotic.oskar.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.crypnotic.oskar.objects.ICommand;
import me.crypnotic.oskar.objects.commands.JoinCommand;
import me.crypnotic.oskar.objects.commands.KickCommand;
import me.crypnotic.oskar.objects.commands.LeaveCommand;
import me.crypnotic.oskar.objects.commands.PauseCommand;
import me.crypnotic.oskar.objects.commands.PlayCommand;
import me.crypnotic.oskar.objects.commands.SkipCommand;
import me.crypnotic.oskar.objects.commands.VolumeCommand;
import me.crypnotic.oskar.utilities.Multisets;
import sx.blah.discord.handle.obj.IMessage;

public class CommandManager {

	private Map<String, ICommand> commands;

	public CommandManager() {
		this.commands = new HashMap<String, ICommand>();
	}

	public void init() {
		commands.put("join", new JoinCommand());
		commands.put("kick", new KickCommand());
		commands.put("leave", new LeaveCommand());
		commands.put("pause", new PauseCommand());
		commands.put("play", new PlayCommand());
		commands.put("skip", new SkipCommand());
		commands.put("volume", new VolumeCommand());
	}

	public void handle(IMessage message, String[] data) {
		Optional<ICommand> result = getCommand(data[0].toLowerCase());
		if (result.isPresent()) {
			try {
				result.get().execute(message, Multisets.transform(data, 1), System.currentTimeMillis());
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			try {
				message.reply("Unknown command : " + data[0]);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public Optional<ICommand> getCommand(String name) {
		ICommand command = commands.get(name);
		if (command != null) {
			return Optional.of(command);
		}
		return Optional.empty();
	}
}
