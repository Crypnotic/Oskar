package me.crypnotic.oskar.managers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import me.crypnotic.oskar.Oskar;
import me.crypnotic.oskar.OskarBootstrap;
import me.crypnotic.oskar.objects.OskarCallback;
import me.crypnotic.oskar.objects.constants.Outcome;
import me.crypnotic.oskar.objects.download.DownloadRequest;
import me.crypnotic.oskar.objects.download.DownloadResponse;
import me.crypnotic.oskar.utilities.Files;
import me.crypnotic.oskar.utilities.Streams;

public class DownloadManager {

	private static final String DOWNLOAD = "http://www.youtubeinmp3.com/fetch/?bitrate=96&video=https://www.youtube.com/watch?v=";
	private static final String PLAYLIST = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&key=AIzaSyCg3WitBUQl5ifC2QygQaZUPOSRMKfSD5E&maxResults=50&playlistId=";

	private Oskar oskar = OskarBootstrap.getOskar();

	private Boolean running;
	private Thread thread;
	private List<DownloadRequest> requests;

	public DownloadManager() {
		this.running = true;
		this.requests = new ArrayList<DownloadRequest>();
	}

	public void init() {
		this.thread = new Thread(() -> {
			Optional<File> cache = oskar.getVideoCache();
			while (running) {
				if (requests.isEmpty()) {
					try {
						Thread.sleep(1000);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				} else {
					List<DownloadRequest> clone = new ArrayList<DownloadRequest>(requests);
					if (cache.isPresent()) {
						downloads: for (DownloadRequest request : clone) {
							try {
								if (request.isPlaylist()) {
									try {
										requests.remove(request);
										getPlaylistVideos(request.getVideoId(), request.getCallback())
												.forEach(this::place);
									} catch (Exception exception) {
										exception.printStackTrace();
										continue downloads;
									}
								}
								File output = new File(cache.get(), request.getVideoId() + ".mp3");
								if (output.exists()) {
									request.getCallback().call(new DownloadResponse(request.getVideoId(),
											Optional.of(output), Outcome.SUCCESSFUL));
									requests.remove(request);
									continue downloads;
								}
								try {
									URL url = new URL(DOWNLOAD + request.getVideoId());
									HttpURLConnection connection = (HttpURLConnection) url.openConnection();
									if (connection.getResponseCode() != 200) {
										continue downloads;
									}
									BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
									FileOutputStream target = new FileOutputStream(output);
									long size = connection.getContentLength();
									long downloaded = 0L;
									int length = -1;
									byte[] buffer = new byte[1024];
									while ((length = input.read(buffer)) > -1) {
										target.write(buffer, 0, length);
										downloaded += length;
									}
									if (input != null) {
										input.close();
									}
									if (output != null) {
										target.close();
									}

									if (output.exists()) {
										if (downloaded == size) {
											request.getCallback().call(new DownloadResponse(request.getVideoId(),
													Optional.of(output), Outcome.SUCCESSFUL));
											requests.remove(request);
											continue downloads;
										} else {
											Files.delete(output);
											requests.remove(request);
											continue downloads;
										}
									}
								} catch (Exception exception) {
									exception.printStackTrace();
									continue downloads;
								}
							} catch (Exception exception) {
								exception.printStackTrace();
								continue downloads;
							}
						}
					} else {
						clone.forEach((request) -> {
							request.getCallback().call(
									new DownloadResponse(request.getVideoId(), Optional.empty(), Outcome.FAILURE));
							requests.remove(request);
						});
					}
				}
			}
		});
		thread.start();

	}

	public void place(DownloadRequest request) {
		this.requests.add(request);
	}

	public void stop() {
		this.running = false;
	}

	private List<DownloadRequest> getPlaylistVideos(String playlistId, OskarCallback<DownloadResponse> callback)
			throws Exception {
		List<DownloadRequest> results = new ArrayList<DownloadRequest>();
		Optional<String> data = Streams.read(PLAYLIST + playlistId);
		if (!data.isPresent()) {
			return results;
		}
		JSONObject json = new JSONObject(new JSONTokener(data.get()));
		JSONArray items = json.getJSONArray("items");
		for (int i = 0; i < items.length(); i++) {
			JSONObject object = items.getJSONObject(i);
			JSONObject snippet = object.getJSONObject("snippet");
			JSONObject resources = snippet.getJSONObject("resourceId");
			String videoId = resources.getString("videoId");
			results.add(new DownloadRequest(videoId, false, callback));
		}
		if (json.get("nextPageToken") != null) {
			String token = json.getString("nextPageToken");
			while (token != null && !json.getString("nextPageToken").equals(token)) {
				data = Streams.read(PLAYLIST + playlistId + "&nextPageToken=" + json.getString("nextPageToken"));
				if (!data.isPresent()) {
					return results;
				}
				json = new JSONObject(new JSONTokener(data.get()));
				items = json.getJSONArray("items");
				for (int i = 0; i < items.length(); i++) {
					JSONObject object = items.getJSONObject(i);
					JSONObject snippet = object.getJSONObject("snippet");
					JSONObject resources = snippet.getJSONObject("resourceId");
					String videoId = resources.getString("videoId");
					results.add(new DownloadRequest(videoId, false, callback));
				}
			}
		}
		return results;
	}
}
