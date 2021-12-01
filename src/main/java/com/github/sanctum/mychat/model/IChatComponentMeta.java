package com.github.sanctum.mychat.model;

import com.github.sanctum.labyrinth.library.ListUtils;
import com.github.sanctum.mychat.MyChat;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object that encapsulates appendable components for chat message formatting.
 */
public class IChatComponentMeta {

	private final String group;

	private final int key;

	public IChatComponentMeta(String group, int key) {
		this.group = group;
		this.key = key;
	}

	/**
	 * @return The group this component meta belongs to.
	 */
	public @NotNull
	String getGroup() {
		return group;
	}

	/**
	 * @return The importance of this meta for appending order.
	 */
	public int getKey() {
		return key;
	}

	/**
	 * @return The optional command to run when the player clicks the {@link IChatComponentMeta#getHoverText()}
	 */
	public @Nullable String getAction() {
		return MyChat.getFormatFile().getRoot().getString(group + "." + key + ".action");
	}

	/**
	 * @return The optional command/text suggestion to offer the player who clicked.
	 */
	public @Nullable String getSuggestion() {
		return MyChat.getFormatFile().getRoot().getString(group + "." + key + ".suggest");
	}

	/**
	 * @return The optional url to open when clicking on this meta.
	 */
	public @Nullable String getUrl() {
		return MyChat.getFormatFile().getRoot().getString(group + "." + key + ".url");
	}

	/**
	 * @return The optional permission node required to be able to view this meta information.
	 */
	public @Nullable String getPermission() {
		return MyChat.getFormatFile().getRoot().getString(group + "." + key + ".node");
	}

	/**
	 * @return The optional prefix to be appended before the meta. (no actions mappable, plain text)
	 */
	public @Nullable String getPrefix() {
		return MyChat.getFormatFile().getRoot().getString(group + "." + key + ".prefix");
	}

	/**
	 * @return The optional suffix to be appended after the meta. (no actions mappable, plain text)
	 */
	public @Nullable String getSuffix() {
		return MyChat.getFormatFile().getRoot().getString(group + "." + key + ".suffix");
	}

	/**
	 * @return The text that solely activates the desired actions.
	 */
	public @NotNull	String getHoverText() {
		return MyChat.getFormatFile().getRoot().getString(group + "." + key + ".text");
	}

	/**
	 * @return The list of information to be appended to this meta's hover message.
	 */
	public @NotNull List<String> getHoverMeta() {

		return ListUtils.use(MyChat.getFormatFile().getRoot().getStringList(group + "." + key + ".hover")).append(s -> "\n");
	}

}
