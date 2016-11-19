package me.crypnotic.oskar;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import me.crypnotic.oskar.managers.CommandManager;
import me.crypnotic.oskar.managers.DownloadManager;
import me.crypnotic.oskar.managers.VoiceManager;
import me.crypnotic.oskar.objects.EventListener;
import me.crypnotic.oskar.objects.JSONConfig;
import me.crypnotic.oskar.objects.constants.Outcome;
import me.crypnotic.oskar.utilities.Files;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class Oskar {

	@Getter(lombok.AccessLevel.PUBLIC)
	private Logger logger;
	@Getter(lombok.AccessLevel.PUBLIC)
	private JSONConfig config;
	@Getter(lombok.AccessLevel.PUBLIC)
	private IDiscordClient discord;
	@Getter(lombok.AccessLevel.PUBLIC)
	private CommandManager commandManager;
	@Getter(lombok.AccessLevel.PUBLIC)
	private DownloadManager downloadManager;
	@Getter(lombok.AccessLevel.PUBLIC)
	private VoiceManager voiceManager;

	public Outcome start() {
		this.logger = LoggerFactory.getLogger("Oskar");

		Optional<File> directory = getDirectory();
		if (directory.isPresent()) {
			Optional<File> file = Files.copy(getClass().getResourceAsStream("/config.json"),
					new File(directory.get().getAbsolutePath(), "config.json"));
			if (file.isPresent()) {
				Optional<FileInputStream> stream = Files.toStream(file.get());
				if (stream.isPresent()) {
					this.config = new JSONConfig(stream.get());
					logger.info("Configuration file loaded.");
				} else {
					logger.info("Configuration file was unsuccessful in loading.");
					return Outcome.FAILURE;
				}
			} else {
				logger.info("Configuration file was unsuccessful in loading.");
				return Outcome.FAILURE;
			}
		}

		if (!config.init().isSuccessful()) {
			logger.info("Configuration file failed to initialize. Exiting...");
			return Outcome.FAILURE;
		}

		this.commandManager = new CommandManager();
		this.downloadManager = new DownloadManager();
		this.voiceManager = new VoiceManager();

		downloadManager.init();
		commandManager.init();

		ClientBuilder builder = new ClientBuilder();
		try {
			this.discord = builder.withToken(config.get("authentication-token").toString()).login();
		} catch (DiscordException exception) {
			exception.printStackTrace();
		}

		discord.getDispatcher().registerListener(new EventListener());

		return Outcome.SUCCESSFUL;
	}

	public Optional<File> getDirectory() {
		try {
			return Optional.of(
					new File(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
							.getParent()));
		} catch (Exception exception) {
			return Optional.empty();
		}
	}

	public Optional<File> getVideoCache() {
		Optional<File> directory = getDirectory();
		if (directory.isPresent()) {
			File cache = new File(directory.get(), "cache");
			if (!cache.exists()) {
				cache.mkdirs();
			}
			return Optional.of(cache);
		} else {
			return Optional.empty();
		}
	}

	public Outcome stop() {
		return Outcome.SUCCESSFUL;
	}
}
