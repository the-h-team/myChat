package com.github.sanctum.mychat.model;

import org.bukkit.entity.Player;

/**
 * An object that encapsulates recorded chat history values.
 */
public class IChatComponentTrail {

	private final Player sender;
	private final String message;
	private final AsyncChatChannel channel;
	private final int key;

	public IChatComponentTrail(Player sender, AsyncChatChannel chatChannel, String message, int key) {
		this.sender = sender;
		this.key = key;
		this.channel = chatChannel;
		this.message = message;
	}

	/**
	 * @return The channel associated with this recorded chat message.
	 */
	public AsyncChatChannel getChannel() {
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
}
