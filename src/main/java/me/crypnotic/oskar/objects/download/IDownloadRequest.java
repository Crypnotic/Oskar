package me.crypnotic.oskar.objects.download;

import java.util.List;

import sx.blah.discord.handle.obj.IMessage;

public interface IDownloadRequest {

	String getId();

	IMessage getMessage();

	Boolean isPlaylist();

	void call(List<DownloadResponse> responses);
}
