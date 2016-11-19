package me.crypnotic.oskar.objects.commands;

import java.util.ArrayList;
import java.util.List;

import me.crypnotic.oskar.objects.ICommand;
import me.crypnotic.oskar.objects.constants.Outcome;
import me.crypnotic.oskar.utilities.Multisets;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class KickCommand implements ICommand {

	@Override
	public Outcome execute(IMessage message, List<String> arguments, Long timestamp)
			throws DiscordException, MissingPermissionsException, RateLimitException {
		if (message.getGuild() != null) {
			if (arguments.size() > 0) {
				IGuild server = message.getGuild();
				List<IUser> users = message.getMentions();
				List<String> names = new ArrayList<String>();
				users.forEach((user) -> {
					if (!user.equals(message.getAuthor())) {
						try {
							server.kickUser(user);
							names.add(user.getName());
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
				if (names.size() > 0) {
					message.reply("users kicked: `" + Multisets.join(names) + "`");
				} else {
					message.reply("none of the specified users were kicked.");
				}
			} else {
				message.reply("command usage: `.kick (users)`");
			}
		} else {
			message.reply("this command does not work in private channels.");
		}
		return Outcome.SUCCESSFUL;
	}
}
