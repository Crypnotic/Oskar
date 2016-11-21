package me.crypnotic.oskar.objects;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.json.JSONTokener;

import me.crypnotic.oskar.Oskar;
import me.crypnotic.oskar.OskarBootstrap;
import me.crypnotic.oskar.utilities.Interwebs;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.VoiceDisconnectedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.AudioPlayer.Track;
import sx.blah.discord.util.audio.events.TrackStartEvent;

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

	@EventSubscriber
	public void onAudioPlayerUpdate(TrackStartEvent event) {
		Track track = event.getTrack();
		AudioPlayer player = event.getPlayer();
		IGuild guild = player.getGuild();
		List<IChannel> channel = guild.getChannelsByName("general");
		if (!channel.isEmpty()) {
			try {
				Map<String, Object> metadata = track.getMetadata();
				File file = (File) metadata.get("file");
				String videoId = file.getName().split("\\.")[0];
				Optional<String> result = Interwebs.read(
						"https://www.youtube.com/oembed?format=json&url=https://www.youtube.com/watch?v=" + videoId);
				if (result.isPresent()) {
					JSONObject json = new JSONObject(new JSONTokener(result.get()));
					channel.get(0).sendMessage("Now Playing: `" + json.getString("title") + "`");
				}
			} catch (MissingPermissionsException | RateLimitException | DiscordException exception) {
				exception.printStackTrace();
			}
		}
	}
}
