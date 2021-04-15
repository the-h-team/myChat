package com.github.sanctum.mychat;

import com.github.sanctum.myessentials.model.CommandData;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public enum MyChatCommand implements CommandData {
	CHAT, MESSAGE, REPLY, MUTE, MAIL;

	@Override
	public @NotNull String getLabel() {
		String result = "";
		switch (this) {
			case MESSAGE:
				result = "message";
				break;
			case REPLY:
				result = "reply";
				break;
			case CHAT:
				result = "chat";
				break;
			case MUTE:
				result = "mute";
				break;
			case MAIL:
				result = "mail";
				break;
		}
		return result;
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = new ArrayList<>();
		switch (this) {
			case REPLY:
				aliases.add("r");
				break;
			case MESSAGE:
				aliases.add("msg");
				aliases.add("tell");
				break;
			case CHAT:
				aliases.add("chat");
				break;
			case MUTE:
				aliases.add("mute");
				aliases.add("silence");
				break;
			case MAIL:
				aliases.add("letters");
				break;
		}
		return aliases;
	}

	@Override
	public @NotNull String getUsage() {
		String result = "";
		switch (this) {
			case CHAT:
				result = "/chat <sub-command>";
				break;
			case MUTE:
				result = "/mute <playerName> | *optional <time>";
				break;
			case MESSAGE:
				result = "/message <playerName> <message...>";
				break;
			case REPLY:
				result = "/reply <message...>";
				break;
			case MAIL:
				result = "/mail <playerName>";
				break;
		}
		return result;
	}

	@Override
	public @NotNull String getDescription() {
		String result = "";
		switch (this) {
			case REPLY:
				result = "Reply to a recent message sent to you.";
				break;
			case MESSAGE:
				result = "Send a message to a player, if the player isn't online it will be sent to their mailbox.";
				break;
			case CHAT:
				result = "The primary command for chat management.";
				break;
			case MUTE:
				result = "Mute a specified player in chat.";
				break;
			case MAIL:
				result = "View sent mail from a specified player.";
				break;
		}
		return result;
	}

	@Override
	public String getPermissionNode() {
		String result = "";
		switch (this) {
			case MUTE:
				result = "mess.chat.mute.player";
				break;
			case CHAT:
				result = "mess.chat";
				break;
			case MESSAGE:
				result = "mess.chat.message";
				break;
			case REPLY:
				result = "mess.chat.reply";
				break;
		}
		return result;
	}



}
