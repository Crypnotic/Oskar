package me.crypnotic.oskar.objects.download.request;

import java.util.List;

import lombok.Getter;
import me.crypnotic.oskar.Oskar;
import me.crypnotic.oskar.OskarBootstrap;
import me.crypnotic.oskar.objects.download.DownloadResponse;
import me.crypnotic.oskar.objects.download.IDownloadRequest;
import sx.blah.discord.handle.obj.IMessage;

public class VideoDownloadRequest implements IDownloadRequest {

	private Oskar oskar = OskarBootstrap.getOskar();

	@Getter(lombok.AccessLevel.PUBLIC)
	private String id;
	@Getter(lombok.AccessLevel.PUBLIC)
	private IMessage message;

	public VideoDownloadRequest(String id, IMessage message) {
		this.id = id;
		this.message = message;
	}

	@Override
	public Boolean isPlaylist() {
		return false;
	}

	@Override
	public void call(List<DownloadResponse> responses) {
		responses.forEach((response) -> {
			try {
				oskar.getVoiceManager().play(message.getGuild(), response.getValue());
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});
	}
}
