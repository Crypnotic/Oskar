package me.crypnotic.oskar.objects.download;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.crypnotic.oskar.objects.OskarCallback;

@AllArgsConstructor
public class DownloadRequest {

	@Getter(lombok.AccessLevel.PUBLIC)
	private String videoId;
	@Getter(lombok.AccessLevel.PUBLIC)
	private OskarCallback<DownloadResponse> callback;
}
