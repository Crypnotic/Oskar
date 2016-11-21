package me.crypnotic.oskar.objects.commands;

import java.util.List;

import me.crypnotic.oskar.Oskar;
import me.crypnotic.oskar.OskarBootstrap;
import me.crypnotic.oskar.objects.ICommand;
import me.crypnotic.oskar.objects.constants.Outcome;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class VolumeCommand implements ICommand {

	private Oskar oskar = OskarBootstrap.getOskar();

	@Override
	public Outcome execute(IMessage message, List<String> arguments, Long timestamp)
			throws DiscordException, MissingPermissionsException, RateLimitException {
		if (oskar.getDiscord().getConnectedVoiceChannels().size() > 0) {
			if (arguments.size() > 0) {
				String data = arguments.get(0);
				if (data.matches("^\\d*\\.?\\d*$")) {
					Float volume = Float.parseFloat(data);
					Outcome outcome = oskar.getVoiceManager().setVolume(message.getGuild(),
							(volume) >= 100 ? 100 : (volume) <= 0 ? 0 : volume);
					if (outcome.getMessage().isPresent()) {
						message.reply(outcome.getMessage().get());
					}
				} else {
					message.reply("command usage: `.volume (1-100)`");
				}
			} else {
				message.reply("command usage: `.volume (1-100)`");
			}
		} else {
			message.reply("I am not currently connected to any voice channels in this guild.");
		}
		return Outcome.SUCCESSFUL;
	}
}
