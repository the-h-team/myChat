package com.github.sanctum.mychat.model;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * An object that encapsulates recorded chat history values.
 */
public class IChatComponentTrail implements Comparable<IChatComponentTrail> {

	private final Player sender;
	private final String message;
	private final IChatChannel channel;
	private final int key;

	public IChatComponentTrail(Player sender, IChatChannel chatChannel, String message, int key) {
		this.sender = sender;
		this.key = key;
		this.channel = chatChannel;
		this.message = message;
	}

	/**
	 * @return The channel associated with this recorded chat message.
	 */
	public IChatChannel getChannel() {
		return channel;
	}

	/**
	 * @return The key identifier for this chat message.
	 */
	public int getKey() {
		return key;
	}

	/**
	 * @return The contents of the message sent.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return The player who sent the message.
	 */
	public Player getSender() {
		return sender;
	}


	@Override
	public int compareTo(@NotNull IChatComponentTrail o) {
		return Integer.compare(getKey(), o.getKey());
	}
}
