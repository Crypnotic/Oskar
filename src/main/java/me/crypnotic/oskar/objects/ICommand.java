package me.crypnotic.oskar.objects;

import java.util.List;

import me.crypnotic.oskar.objects.constants.Outcome;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public interface ICommand {

	Outcome execute(IMessage message, List<String> arguments, Long timestamp) throws DiscordException, MissingPermissionsException, RateLimitException;
}
