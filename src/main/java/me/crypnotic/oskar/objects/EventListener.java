package me.crypnotic.oskar.objects;

import me.crypnotic.oskar.Oskar;
import me.crypnotic.oskar.OskarBootstrap;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.VoiceDisconnectedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class EventListener {

	private Oskar oskar = OskarBootstrap.getOskar();

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		if (message.getContent().matches("^\\.([a-zA-Z]+)(.*)$")) {
			oskar.getCommandManager().handle(message, message.getContent().substring(1).split(" "));
		}
	}

	@EventSubscriber
	public void onVoiceDisconnected(VoiceDisconnectedEvent event) {
		event.getClient().getGuilds().forEach((guild) -> {
			oskar.getVoiceManager().stop(guild);
		});
	}
}
