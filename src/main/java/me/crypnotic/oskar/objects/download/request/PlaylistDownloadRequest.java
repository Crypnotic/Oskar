package me.crypnotic.oskar.objects.download.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.crypnotic.oskar.Oskar;
import me.crypnotic.oskar.OskarBootstrap;
import me.crypnotic.oskar.objects.constants.Outcome;
import me.crypnotic.oskar.objects.download.DownloadResponse;
import me.crypnotic.oskar.objects.download.IDownloadRequest;
import me.crypnotic.oskar.utilities.Multisets;
import sx.blah.discord.handle.obj.IMessage;

public class PlaylistDownloadRequest implements IDownloadRequest {

	private Oskar oskar = OskarBootstrap.getOskar();

	@Getter(lombok.AccessLevel.PUBLIC)
	private String id;
	@Getter(lombok.AccessLevel.PUBLIC)
	private IMessage message;

	public PlaylistDownloadRequest(String id, IMessage message) {
		this.id = id;
		this.message = message;
	}

	@Override
	public Boolean isPlaylist() {
		return true;
	}

	@Override
	public void call(List<DownloadResponse> responses) {
		List<String> successes = new ArrayList<>();
		List<String> failures = new ArrayList<>();

		responses.forEach((response) -> {
			if (response.getOutcome().isSuccessful()) {
				Outcome outcome = oskar.getVoiceManager().play(message.getGuild(), response.getValue());
				if (outcome.isSuccessful()) {
					successes.add(response.getVideoId());
				} else {
					failures.add(response.getVideoId());
				}
			} else {
				failures.add(response.getVideoId());
			}
		});

		try {
			if (successes.size() > 0) {
				message.reply(String.format("`%d` tracks were queued: ```%s```", successes.size(),
						Multisets.join(successes, ", ")));
			}
			if (failures.size() > 0) {
				message.reply(String.format("`%d` tracks could not be queued: ```%s```", failures.size(),
						Multisets.join(failures, ", ")));
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
