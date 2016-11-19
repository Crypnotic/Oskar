package me.crypnotic.oskar.objects.commands;

import java.util.ArrayList;
import java.util.List;

import me.crypnotic.oskar.Oskar;
import me.crypnotic.oskar.OskarBootstrap;
import me.crypnotic.oskar.objects.ICommand;
import me.crypnotic.oskar.objects.constants.Outcome;
import me.crypnotic.oskar.utilities.Multisets;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class LeaveCommand implements ICommand {

	private Oskar oskar = OskarBootstrap.getOskar();

	@Override
	public Outcome execute(IMessage message, List<String> arguments, Long timestamp)
			throws MissingPermissionsException, RateLimitException, DiscordException {
		List<IVoiceChannel> channels = oskar.getDiscord().getConnectedVoiceChannels();
		if (!channels.isEmpty()) {
			List<String> names = new ArrayList<String>();
			channels.forEach((channel) -> {
				if (channel.getGuild() == message.getGuild()) {
					channel.leave();
					names.add(channel.getName());
				}
			});
			if (names.size() > 0) {
				message.reply("channels left: `" + Multisets.join(names) + "`");
			} else {
				message.reply("I am not currently connected to any voice channels in this guild.");
			}
		} else {
			message.reply("I am not currently connected to any voice channels in this guild.");
		}
		return Outcome.SUCCESSFUL;
	}
}
