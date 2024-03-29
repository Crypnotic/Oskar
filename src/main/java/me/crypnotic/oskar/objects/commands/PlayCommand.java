package me.crypnotic.oskar.objects.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.crypnotic.oskar.Oskar;
import me.crypnotic.oskar.OskarBootstrap;
import me.crypnotic.oskar.objects.ICommand;
import me.crypnotic.oskar.objects.constants.Outcome;
import me.crypnotic.oskar.objects.download.request.PlaylistDownloadRequest;
import me.crypnotic.oskar.objects.download.request.VideoDownloadRequest;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class PlayCommand implements ICommand {

	private Oskar oskar = OskarBootstrap.getOskar();

	@Override
	public Outcome execute(IMessage message, List<String> arguments, Long timestamp)
			throws MissingPermissionsException, RateLimitException, DiscordException {
		if (oskar.getDiscord().getConnectedVoiceChannels().size() > 0) {
			if (arguments.size() > 0) {
				String name = arguments.get(0);
				Pattern pattern = Pattern.compile(
						"^((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be))(\\/(?:[\\w\\-]+(?:\\?v=|\\?list=)|embed\\/|v\\/)?)([\\w\\-]+)(\\S+)?$",
						Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(name);
				if (matcher.matches()) {
					String type = matcher.group(4).toLowerCase();
					String videoId = matcher.group(5);
					if (type.contains("playlist")) {
						oskar.getDownloadManager().place(new PlaylistDownloadRequest(videoId, message));
					} else {
						oskar.getDownloadManager().place(new VideoDownloadRequest(videoId, message));
					}
				} else {
					message.reply("command usage: `.play (youtube link)`");
				}
			} else {
				message.reply("command usage: `.play (youtube link)`");
			}
		} else {
			message.reply("I am not currently connected to any voice channels in this guild.");
		}
		return Outcome.SUCCESSFUL;
	}
}
