package me.crypnotic.oskar.objects.commands;

import java.util.List;

import me.crypnotic.oskar.objects.ICommand;
import me.crypnotic.oskar.objects.constants.Outcome;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class JoinCommand implements ICommand {

	@Override
	public Outcome execute(IMessage message, List<String> arguments, Long timestamp)
			throws MissingPermissionsException, RateLimitException, DiscordException {
		IUser user = message.getAuthor();
		if (user != null) {
			if (user.getConnectedVoiceChannels().size() > 0) {
				IVoiceChannel channel = user.getConnectedVoiceChannels().get(0);
				channel.join();
				message.reply("joined channel `" + channel.getName() + "`");
			} else {
				message.reply("you are not currently connected to a voice channel");
			}
		} else {
			message.reply("this wasn't supposed to happen ;(");
		}
		return Outcome.SUCCESSFUL;
	}
}
