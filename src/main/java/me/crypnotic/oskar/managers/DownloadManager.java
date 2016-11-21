package me.crypnotic.oskar.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import me.crypnotic.oskar.Oskar;
import me.crypnotic.oskar.OskarBootstrap;
import me.crypnotic.oskar.objects.constants.Outcome;
import me.crypnotic.oskar.objects.download.DownloadResponse;
import me.crypnotic.oskar.objects.download.IDownloadRequest;
import me.crypnotic.oskar.utilities.Interwebs;

public class DownloadManager {

	private static final String DOWNLOAD = "http://www.youtubeinmp3.com/fetch/?bitrate=96&video=https://www.youtube.com/watch?v=";
	private static final String PLAYLIST = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&key=AIzaSyCg3WitBUQl5ifC2QygQaZUPOSRMKfSD5E&maxResults=50&playlistId=";

	private Oskar oskar = OskarBootstrap.getOskar();

	private Boolean running;
	private Thread thread;
	private List<IDownloadRequest> requests;

	public DownloadManager() {
		this.running = true;
		this.requests = new ArrayList<IDownloadRequest>();
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
					List<IDownloadRequest> clone = new ArrayList<IDownloadRequest>(requests);
					if (cache.isPresent()) {
						downloads: for (IDownloadRequest request : clone) {
							try {
								if (request.isPlaylist()) {
									request.getMessage().reply("currently working on playlist: " + request.getId());
									try {
										List<DownloadResponse> results = new ArrayList<DownloadResponse>();
										Optional<String> data = Interwebs.read(PLAYLIST + request.getId());
										if (!data.isPresent()) {
											requests.remove(request);
											continue downloads;
										}
										JSONObject json = new JSONObject(new JSONTokener(data.get()));
										JSONArray items = json.getJSONArray("items");
										for (int i = 0; i < items.length(); i++) {
											JSONObject object = items.getJSONObject(i);
											JSONObject snippet = object.getJSONObject("snippet");
											JSONObject resources = snippet.getJSONObject("resourceId");
											String videoId = resources.getString("videoId");
											File output = new File(cache.get(), videoId + ".mp3");
											Optional<File> file = Interwebs.download(DOWNLOAD + videoId, output);
											if (file.isPresent()) {
												results.add(new DownloadResponse(videoId, file, Outcome.SUCCESSFUL));
											} else {
												results.add(new DownloadResponse(videoId, file, Outcome.FAILURE));
											}
										}
										if (json.has("nextPageToken")) {
											boolean first = true;
											String token = json.getString("nextPageToken");
											while (first || (token != null
													&& !json.getString("nextPageToken").equals(token))) {
												first = false;
												data = Interwebs.read(PLAYLIST + request.getId() + "&pageToken="
														+ json.getString("nextPageToken"));
												if (!data.isPresent()) {
													requests.remove(request);
													continue downloads;
												}
												json = new JSONObject(new JSONTokener(data.get()));
												token = json.getString("nextPageToken");
												items = json.getJSONArray("items");
												for (int i = 0; i < items.length(); i++) {
													JSONObject object = items.getJSONObject(i);
													JSONObject snippet = object.getJSONObject("snippet");
													JSONObject resources = snippet.getJSONObject("resourceId");
													String videoId = resources.getString("videoId");
													File output = new File(cache.get(), videoId + ".mp3");
													Optional<File> file = Interwebs.download(DOWNLOAD + videoId,
															output);
													if (file.isPresent()) {
														results.add(new DownloadResponse(videoId, file,
																Outcome.SUCCESSFUL));
													} else {
														results.add(
																new DownloadResponse(videoId, file, Outcome.FAILURE));
													}
												}
											}
										}
										request.call(results);
									} catch (Exception exception) {
										exception.printStackTrace();
									}
									requests.remove(request);
									continue downloads;
								} else {
									request.getMessage().reply("currently working on: " + request.getId());

									File output = new File(cache.get(), request.getId() + ".mp3");
									Optional<File> file = Interwebs.download(DOWNLOAD + request.getId(), output);
									if (file.isPresent()) {
										request.call(Arrays.asList(new DownloadResponse(request.getId(),
												Optional.of(output), Outcome.SUCCESSFUL)));
										requests.remove(request);
										continue downloads;
									}
								}
							} catch (Exception exception) {
								exception.printStackTrace();
								continue downloads;
							}
						}
					} else {
						running = false;
						oskar.getLogger().info("The audio file cache does not exist! Exiting...");
					}
				}
			}
		});
		thread.start();

	}

	public void place(IDownloadRequest request) {
		this.requests.add(request);
	}

	public void stop() {
		this.running = false;
	}
}
