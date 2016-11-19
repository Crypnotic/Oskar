package me.crypnotic.oskar.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sound.sampled.UnsupportedAudioFileException;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;

public class VoiceManager {

	private Map<IGuild, AudioPlayer> players;

	public VoiceManager() {
		this.players = new HashMap<IGuild, AudioPlayer>();
	}

	public synchronized AudioPlayer getAudioPlayer(IGuild guild) {
		AudioPlayer player = players.get(guild);
		if (player == null) {
			player = AudioPlayer.getAudioPlayerForGuild(guild);
			players.put(guild, player);
		}
		return player;
	}

	public void play(IMessage message, Optional<File> audio) {
		IGuild guild = message.getGuild();
		AudioPlayer player = getAudioPlayer(guild);
		if (audio.isPresent()) {
			try {
				player.queue(audio.get());
				try {
					message.reply("audio file `" + audio.get().getName() + "` has been added to the queue.");
				} catch (MissingPermissionsException | RateLimitException | DiscordException exception) {
					exception.printStackTrace();
				}
			} catch (IOException | UnsupportedAudioFileException exception) {
				exception.printStackTrace();
			}
		} else {
			pause(message);
		}
	}

	public void skip(IMessage message) {
		IGuild guild = message.getGuild();
		AudioPlayer player = getAudioPlayer(guild);
		player.skip();
		try {
			message.reply("skipped current track.");
		} catch (MissingPermissionsException | RateLimitException | DiscordException exception) {
			exception.printStackTrace();
		}
	}

	public void setVolume(IGuild guild, Float volume) {
		AudioPlayer player = getAudioPlayer(guild);
		player.setVolume((volume > 1) ? volume / 100 : volume);
	}

	public void pause(IMessage message) {
		IGuild guild = message.getGuild();
		AudioPlayer player = getAudioPlayer(guild);
		if (player.getPlaylistSize() < 1) {
			try {
				message.reply("there are currently no tracks in the queue.");
			} catch (MissingPermissionsException | RateLimitException | DiscordException exception) {
				exception.printStackTrace();
			}
		} else {
			if (player.isPaused()) {
				player.setPaused(false);
				try {
					message.reply("resuming audio playback.");
				} catch (MissingPermissionsException | RateLimitException | DiscordException exception) {
					exception.printStackTrace();
				}
			} else {
				player.setPaused(true);
				try {
					message.reply("audio playback was paused.");
				} catch (MissingPermissionsException | RateLimitException | DiscordException exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	public void stop(IGuild guild) {
		AudioPlayer player = getAudioPlayer(guild);
		player.setPaused(true);
		players.remove(guild, player);
	}
}
