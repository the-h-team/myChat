package com.github.sanctum.mychat.model;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object that encapsulates custom data for configurable chat channels.
 */
public class AsyncChatChannel {

	private final List<Player> recipients = new ArrayList<>();
	private final String channel;
	private final String permission;
	private final boolean main;
	private boolean muted;

	public AsyncChatChannel(@NotNull String tag, @NotNull String permission, boolean isMain) {
		this.channel = tag;
		this.main = isMain;
		this.permission = permission;
	}

	/**
	 * @return The name of this chat channel.
	 */
	public @NotNull String getChannel() {
		return this.channel;
	}

	/**
	 * @return true if this chat channel is the primary "global" channel.
	 */
	public boolean isDefault() {
		return main;
	}

	/**
	 * @param muted The state to set chat activity to.
	 */
	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	/**
	 * @return true if the chat channel is currently muted.
	 */
	public boolean isMuted() {
		return muted;
	}

	/**
	 * @param target The target to clear chat for.
	 */
	public void clear(Player target) {
		for (int i = 0; i < 100; i++) {
			target.sendMessage("");
		}
	}

	/**
	 * Clear the chat for all active channel users.
	 */
	public void clear() {
		for (Player recipient : recipients) {
			clear(recipient);
		}
	}

	/**
	 * @return The user's currently active in this chat channel.
	 */
	public @NotNull List<Player> getUsers() {
		return this.recipients;
	}

	/**
	 * @return The permission node required to participate in this chat channel
	 */
	public @NotNull String getPermission() {
		return permission;
	}

	/**
	 * @param target The target to remove from this chat channel.
	 */
	public void removeUser(Player target) {
		this.recipients.remove(target);
	}

	/**
	 * @param target The target to add to this chat channel.
	 */
	public void addUser(Player target) {
		if (!this.recipients.contains(target)) {
			this.recipients.add(target);
		}
	}




}
