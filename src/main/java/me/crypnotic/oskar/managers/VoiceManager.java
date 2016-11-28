package me.crypnotic.oskar.managers;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.sound.sampled.UnsupportedAudioFileException;

import me.crypnotic.oskar.Oskar;
import me.crypnotic.oskar.OskarBootstrap;
import me.crypnotic.oskar.objects.constants.Outcome;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.audio.AudioPlayer;

public class VoiceManager {

	private static Oskar oskar = OskarBootstrap.getOskar();

	public synchronized AudioPlayer getAudioPlayer(IGuild guild) {
		return AudioPlayer.getAudioPlayerForGuild(guild);
	}

	public Outcome play(IGuild guild, Optional<File> audio) {
		AudioPlayer player = getAudioPlayer(guild);
		if (audio.isPresent()) {
			try {
				player.queue(audio.get());
				return Outcome.SUCCESSFUL;
			} catch (IOException | UnsupportedAudioFileException exception) {
				exception.printStackTrace();
				return Outcome.FAILURE.setMessage(exception.getLocalizedMessage());
			}
		} else {
			pause(guild);
			return Outcome.SUCCESSFUL;
		}
	}

	public Outcome clear(IGuild guild) {
		AudioPlayer player = getAudioPlayer(guild);
		player.clear();
		return Outcome.SUCCESSFUL.setMessage("playlist has been cleared.");
	}

	public Outcome skip(IGuild guild) {
		AudioPlayer player = getAudioPlayer(guild);
		player.skip();
		return Outcome.SUCCESSFUL.setMessage("skipped current track.");
	}

	public Outcome setVolume(IGuild guild, Float volume) {
		AudioPlayer player = getAudioPlayer(guild);
		player.setVolume((volume > 1) ? volume / 100 : volume);
		return Outcome.SUCCESSFUL.setMessage("volume changed to `%d%`", volume.intValue());
	}

	public Outcome pause(IGuild guild) {
		AudioPlayer player = getAudioPlayer(guild);
		if (player.getPlaylistSize() < 1) {
			return Outcome.SUCCESSFUL.setMessage("there are currently no tracks in the queue.");
		} else {
			if (player.isPaused()) {
				player.setPaused(false);
				return Outcome.SUCCESSFUL.setMessage("resuming audio playback.");
			} else {
				player.setPaused(true);
				return Outcome.SUCCESSFUL.setMessage("audio playback was paused.");
			}
		}

	}

	public void stop(IGuild guild) {
		AudioPlayer player = getAudioPlayer(guild);
		player.setPaused(true);
		player.clean();
	}

	public void shutdown() {
		oskar.getDiscord().getGuilds().forEach((guild) -> {
			stop(guild);
		});
	}
}
