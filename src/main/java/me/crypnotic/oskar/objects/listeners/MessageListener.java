package me.crypnotic.oskar.objects.listeners;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import me.crypnotic.oskar.Oskar;

public class MessageListener implements MessageCreateListener {

	private Oskar oskar;

	public MessageListener(Oskar oskar) {
		this.oskar = oskar;
	}

	@Override
	public void onMessageCreate(DiscordAPI child, Message message) {
		if (message.getContent().matches("^\\/([a-zA-Z]+)(.*)$")) {
			oskar.getCommandManager().handle(message, message.getContent().substring(1).split(" "));
		}
	}
}
