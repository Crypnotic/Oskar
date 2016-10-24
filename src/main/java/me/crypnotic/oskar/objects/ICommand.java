package me.crypnotic.oskar.objects;

import java.util.List;

import de.btobastian.javacord.entities.message.Message;
import me.crypnotic.oskar.objects.constants.Outcome;

public interface ICommand {

	Outcome execute(Message message, List<String> arguments, Long timestamp);
}
