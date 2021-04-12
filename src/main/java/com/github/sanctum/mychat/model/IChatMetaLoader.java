package com.github.sanctum.mychat.model;

import com.github.sanctum.mychat.util.ChatComponentUtil;
import com.github.sanctum.mychat.util.TablistDisplay;
import com.github.sanctum.myessentials.Essentials;
import com.github.sanctum.myessentials.api.MyEssentialsAPI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class IChatMetaLoader {
	public final Map<String, List<IChatComponentMeta>> META_MAP = new HashMap<>();
	public final Map<Integer, IChatComponentTrail> RESEND_MAP = new HashMap<>();
	public final Map<Integer, List<String>> HEADER = new HashMap<>();
	public final Map<Integer, List<String>> FOOTER = new HashMap<>();
	public final LinkedList<AsyncChatChannel> CHAT_CHANNELS = new LinkedList<>();
	public final LinkedList<IChatCooldownOffense> OFFENSE_LOG = new LinkedList<>();
	public final LinkedList<TablistDisplay> DISPLAY_HEADER = new LinkedList<>();
	public final LinkedList<TablistDisplay> DISPLAY_FOOTER = new LinkedList<>();
	public final LinkedList<Player> SPY_LOG = new LinkedList<>();
	public boolean RELOADED = false;

	public @Nullable
	AsyncChatChannel getChannel(Player target) {
		return CHAT_CHANNELS.stream().filter(c -> c.getUsers().contains(target)).findFirst().orElse(null);
	}

	public IChatCooldownOffense getOffense(Player target) {
		return OFFENSE_LOG.stream().filter(o -> o.getOffender() == target).findFirst().orElse(null);
	}

	public LinkedList<AsyncChatChannel> getChatChannels() {
		return CHAT_CHANNELS;
	}

	public String getChatColor(OfflinePlayer target) {
		return Optional.ofNullable(MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data").getConfig().getString(target.getUniqueId().toString())).orElse("&r");
	}

	public LinkedList<IChatComponentMeta> getMeta(String group) {
		if (META_MAP.get(group) == null) {
			Essentials.getInstance().getLogger().warning("- The fallback group is being used. Group not detected in found groups. A configuration reload is necessary.");
			group = ChatComponentUtil.getFallback();
		}
		List<IChatComponentMeta> META = new ArrayList<>(META_MAP.get(group));
		META.sort(Comparator.comparingInt(IChatComponentMeta::getKey));
		return new LinkedList<>(META);
	}

	public IChatComponentTrail getMessage(int key) {
		return RESEND_MAP.get(key);
	}

}
