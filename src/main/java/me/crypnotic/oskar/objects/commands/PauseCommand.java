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

public class PauseCommand implements ICommand {

	private Oskar oskar = OskarBootstrap.getOskar();

	@Override
	public Outcome execute(IMessage message, List<String> arguments, Long timestamp)
			throws MissingPermissionsException, RateLimitException, DiscordException {
		if (oskar.getDiscord().getConnectedVoiceChannels().size() > 0) {
			oskar.getVoiceManager().pause(message);
		} else {
			message.reply("I am not currently connected to any voice channels in this guild.");
		}
		return Outcome.SUCCESSFUL;
	}
}
