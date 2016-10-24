package me.crypnotic.oskar;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import lombok.Getter;
import me.crypnotic.oskar.managers.CommandManager;
import me.crypnotic.oskar.objects.constants.Outcome;
import me.crypnotic.oskar.objects.listeners.MessageListener;

public class Oskar {

	private Oskar oskar;
	@Getter(lombok.AccessLevel.PUBLIC)
	private DiscordAPI discord;
	@Getter(lombok.AccessLevel.PUBLIC)
	private CommandManager commandManager;

	public Oskar() {
		this.oskar = this;
		this.discord = Javacord.getApi("MjM4ODgyODUxMjQyNzcwNDM0.CuwsJw.8OEA7YOfMHCLiqYEp2jusyIh3LQ", true);
		this.commandManager = new CommandManager();
	}

	public Outcome start() {
		commandManager.init();

		discord.connect(new FutureCallback<DiscordAPI>() {
			@Override
			public void onSuccess(DiscordAPI result) {
				result.registerListener(new MessageListener(oskar));
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		return Outcome.SUCCESSFUL;
	}

	public Outcome stop() {
		return Outcome.SUCCESSFUL;
	}
}
