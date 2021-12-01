package com.github.sanctum.mychat.model;

import com.github.sanctum.labyrinth.annotation.Ordinal;
import com.github.sanctum.mychat.util.ChatComponentUtil;
import com.github.sanctum.myessentials.Essentials;
import com.github.sanctum.myessentials.api.MyEssentialsAPI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IChatMetaLoader {

	public final Map<Integer, List<String>> HEADER_QUEUE = new HashMap<>();
	public final Map<Integer, List<String>> FOOTER_QUEUE = new HashMap<>();

	private final Map<String, List<IChatComponentMeta>> GROUP_FORMATTING = new HashMap<>();
	private final Map<Integer, IChatComponentTrail> MESSAGE_HISTORY = new HashMap<>();
	private final Map<Integer, IChatComponentTrail> WITHHELD_MESSAGES = new HashMap<>();
	private final List<IChatChannel> CHAT_CHANNELS = new ArrayList<>();
	private final List<IChatCooldownOffense> OFFENSE_LOG = new ArrayList<>();
	private final Set<Player> SPY_LOG = new HashSet<>();

	public IChatChannel getChannel(Player target) {
		return CHAT_CHANNELS.stream().filter(c -> c.getUsers().contains(target)).findFirst().orElse(null);
	}

	public IChatCooldownOffense getOffense(Player target) {
		return OFFENSE_LOG.stream().filter(o -> o.getOffender() == target).findFirst().orElse(null);
	}

	public List<IChatChannel> getChatChannels() {
		return CHAT_CHANNELS;
	}

	public String getChatColor(OfflinePlayer target) {
		return Optional.ofNullable(MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data").getRoot().getString(target.getUniqueId() + ".color")).orElse("");
	}

	public List<IChatComponentMeta> getMeta(String group) {
		if (GROUP_FORMATTING.get(group) == null) {
			Essentials.getInstance().getLogger().warning("- The fallback group is being used. Group not detected in found groups. A configuration reload is necessary.");
			group = ChatComponentUtil.getFallback();
		}
		List<IChatComponentMeta> META = new ArrayList<>(GROUP_FORMATTING.get(group));
		META.sort(Comparator.comparingInt(IChatComponentMeta::getKey));
		return new LinkedList<>(META);
	}

	public IChatComponentTrail getTrail(int key) {
		return MESSAGE_HISTORY.get(key);
	}

	public Collection<IChatComponentTrail> getTrails() {
		return MESSAGE_HISTORY.values();
	}

	public IChatComponentTrail getWitheld(int key) {
		return WITHHELD_MESSAGES.get(key);
	}

	public Collection<IChatComponentTrail> getWitheld() {
		return WITHHELD_MESSAGES.values();
	}

	public Set<Player> getSpies() {
		return Collections.unmodifiableSet(SPY_LOG);
	}

	public IChatMetaLoader loadMeta(@NotNull String group, @NotNull List<IChatComponentMeta> componentMetas) {
		GROUP_FORMATTING.put(group, componentMetas);
		return this;
	}

	public IChatMetaLoader loadChannel(IChatChannel channel) {
		CHAT_CHANNELS.add(channel);
		return this;
	}

	public IChatMetaLoader newOffense(Player player) {
		OFFENSE_LOG.add(new IChatCooldownOffense(player));
		return this;
	}

	public IChatMetaLoader newSpy(Player player) {
		SPY_LOG.add(player);
		return this;
	}

	public IChatMetaLoader removeSpy(Player player) {
		SPY_LOG.remove(player);
		return this;
	}

	public IChatMetaLoader removeOffense(Player player) {
		OFFENSE_LOG.removeIf(o -> o.getOffender().equals(player));
		return this;
	}

	public boolean isSpying(Player player) {
		return SPY_LOG.contains(player);
	}

	@Ordinal
	IChatMetaLoader registerMessage(int id, IChatComponentTrail trail) {
		MESSAGE_HISTORY.put(id, trail);
		return this;
	}

	@Ordinal(5)
	IChatMetaLoader registerWitholding(int id, IChatComponentTrail trail) {
		WITHHELD_MESSAGES.put(id, trail);
		return this;
	}

	@Ordinal(6)
	IChatMetaLoader removeWitholding(int id) {
		WITHHELD_MESSAGES.remove(id);
		return this;
	}

	@Ordinal(2)
	IChatMetaLoader removeMessage(int id) {
		MESSAGE_HISTORY.remove(id);
		return this;
	}

	@Ordinal(3)
	void clearMessages() {
		MESSAGE_HISTORY.clear();
	}

	@Ordinal(4)
	void clearGroupFormatting() {
		GROUP_FORMATTING.clear();
	}

	@Ordinal(7)
	void clearChannels() {
		CHAT_CHANNELS.clear();
	}

}
