package com.github.sanctum.mychat;

import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.myessentials.api.MyEssentialsAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum MySettings {

	MESSAGE_OUT, MESSAGE_OUT_HOVER, MESSAGE_OUT_META, MESSAGE_IN,
	MESSAGE_IN_HOVER, MESSAGE_IN_META, WORD_BLACKLIST, WORD_WITHHOLD_LIST,
	WORD_REPLACEMENTS, TABLIST_USED, TABLIST_WINDBACK, TABLIST_HEADER,
	TABLIST_FOOTER, TABLIST_INTERVAL, TABLIST_PLAYER, COOLDOWN_TIME, OFFENSE_MAX,
	MOTD_SERVER, MOTD_JOIN, JOIN, LEAVE, OFFENSE_MESSAGE, OFFENSE_KICK_MESSAGE;

	private static final FileManager settings = MyEssentialsAPI.getInstance().getAddonFile("Settings", "Chat");

	public Map<Integer, List<String>> grab() {
		Map<Integer, List<String>> result = new HashMap<>();
		switch (this) {
			case TABLIST_HEADER:
				for (String i : settings.getRoot().getNode("tab-list.header").getKeys(false)) {
					result.put(Integer.parseInt(i), settings.getRoot().getStringList("tab-list.header." + i));
				}
				return result;
			case TABLIST_FOOTER:
				for (String i : settings.getRoot().getNode("tab-list.footer").getKeys(false)) {
					result.put(Integer.parseInt(i), settings.getRoot().getStringList("tab-list.footer." + i));
				}
				return result;
		}
		return new HashMap<>();
	}

	public List<String> get() {
		switch (this) {
			case WORD_BLACKLIST:
				return settings.getRoot().getStringList("context.blacklist");
			case WORD_REPLACEMENTS:
				return settings.getRoot().getStringList("context.replacement");
			case WORD_WITHHOLD_LIST:
				return settings.getRoot().getStringList("context.with-held");
			case MOTD_JOIN:
				return settings.getRoot().getStringList("motd.join");
		}
		return new ArrayList<>();
	}

	public int getInt() {
		switch (this) {
			case COOLDOWN_TIME:
				return settings.getRoot().getInt("context.send-cooldown");
			case OFFENSE_MAX:
				return settings.getRoot().getInt("offense.max-before-kick");
			case TABLIST_INTERVAL:
				return settings.getRoot().getInt("tab-list.update-interval");
		}
		return 0;
	}

	public String getString() {
		switch (this) {
			case OFFENSE_MESSAGE:
				return settings.getRoot().getString("offense.fail-message");
			case OFFENSE_KICK_MESSAGE:
				return settings.getRoot().getString("offense.kick-message");
			case TABLIST_PLAYER:
				return settings.getRoot().getString("tab-list.player-name");
			case MESSAGE_IN:
				return settings.getRoot().getString("messaging.in.normal");
			case MESSAGE_IN_HOVER:
				return settings.getRoot().getString("messaging.in.hover");
			case MESSAGE_IN_META:
				return settings.getRoot().getString("messaging.in.meta");
			case MESSAGE_OUT:
				return settings.getRoot().getString("messaging.out.normal");
			case MESSAGE_OUT_HOVER:
				return settings.getRoot().getString("messaging.out.hover");
			case MESSAGE_OUT_META:
				return settings.getRoot().getString("messaging.out.meta");
			case MOTD_SERVER:
				return settings.getRoot().getString("motd.server-list");
			case JOIN:
				return settings.getRoot().getString("activity.join.message");
			case LEAVE:
				return settings.getRoot().getString("activity.leave.message");
		}
		return "";
	}

	public boolean valid() {
		switch (this) {
			case TABLIST_USED:
				return settings.getRoot().getBoolean("tab-list.enabled");
			case JOIN:
				return settings.getRoot().getBoolean("activity.join.enabled");
			case LEAVE:
				return settings.getRoot().getBoolean("activity.leave.enabled");
			case TABLIST_WINDBACK:
				if (!settings.getRoot().exists()) {
					return true;
				}
				return settings.getRoot().getBoolean("tab-list.wind-back");
		}
		return false;
	}

	public static FileManager getSettings() {
		return settings;
	}

	public static void loadDefaults() {
		if (!MySettings.getSettings().getRoot().exists()) {
			FileManager colors = MyEssentialsAPI.getInstance().getAddonFile("Colors", "Chat");
			colors.getRoot().set("a.name", "jolly");
			colors.getRoot().set("a.from", "#5dd473");
			colors.getRoot().set("a.to", "#02f2ce");
			colors.getRoot().set("b.name", "warm");
			colors.getRoot().set("b.from", "#f2be02");
			colors.getRoot().set("b.to", "#f28602");
			colors.getRoot().set("c.name", "nightmare");
			colors.getRoot().set("c.from", "#3d687d");
			colors.getRoot().set("c.to", "#190524");
			colors.getRoot().set("d.name", "turtle");
			colors.getRoot().set("d.from", "#3d7d3d");
			colors.getRoot().set("d.to", "#2aa16f");
			colors.getRoot().set("e.name", "apple");
			colors.getRoot().set("e.from", "#00a60e");
			colors.getRoot().set("e.to", "#48ff00");
			colors.getRoot().set("f.name", "magical");
			colors.getRoot().set("f.from", "#00ffcc");
			colors.getRoot().set("f.to", "#ff00d4");
			colors.getRoot().set("g.name", "frosty");
			colors.getRoot().set("g.from", "#ebffff");
			colors.getRoot().set("g.to", "#ebfff1");
			colors.getRoot().set("h.name", "glazed");
			colors.getRoot().set("h.from", "#ffdc7a");
			colors.getRoot().set("h.to", "#fabc0f");
			colors.getRoot().save();
			FileManager settings = MySettings.getSettings();
			settings.getRoot().set("tab-list.enabled", true);
			settings.getRoot().set("tab-list.wind-back", true);
			settings.getRoot().set("tab-list.update-interval", 40);
			settings.getRoot().set("tab-list.player-name", "{PLAYER}");
			settings.getRoot().set("tab-list.header.1", Arrays.asList("<#32a852>My Server</#8532a8>", "<#32a8a6>▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#a4a832>"));
			settings.getRoot().set("tab-list.header.2", Arrays.asList("<#32a852>My Server</#8532a8>", "<#a4a832>▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#d93316>"));
			settings.getRoot().set("tab-list.header.3", Arrays.asList("<#32a852>My Server</#8532a8>", "<#d93316>▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#b02089>"));
			settings.getRoot().set("tab-list.header.4", Arrays.asList("<#32a852>My Server</#8532a8>", "<#b02089>▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#8520b0>"));
			settings.getRoot().set("tab-list.header.5", Arrays.asList("<#32a852>My Server</#8532a8>", "<#8520b0>▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#6134c9>"));
			settings.getRoot().set("tab-list.header.6", Arrays.asList("<#32a852>My Server</#8532a8>", "<#6134c9>▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#3454c9>"));
			settings.getRoot().set("tab-list.header.7", Arrays.asList("<#32a852>My Server</#8532a8>", "<#3454c9>▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#34b0c9>"));
			settings.getRoot().set("tab-list.header.8", Arrays.asList("<#32a852>My Server</#8532a8>", "<#34b0c9>▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#33b8aa>"));
			settings.getRoot().set("tab-list.header.9", Arrays.asList("<#32a852>My Server</#8532a8>", "<#33b8aa>▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#34e090>"));
			settings.getRoot().set("tab-list.header.10", Arrays.asList("<#32a852>My Server</#8532a8>", "<#34e090>▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#35ba32>"));
			settings.getRoot().set("tab-list.header.11", Arrays.asList("<#32a852>My Server</#8532a8>", "<#35ba32>▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#9cde3a>"));
			settings.getRoot().set("tab-list.header.12", Arrays.asList("<#32a852>My Server</#8532a8>", "<#9cde3a>▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#32a890>"));
			settings.getRoot().set("tab-list.footer.1", Collections.singletonList("<#32a8a6>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬</#a4a832>"));
			settings.getRoot().set("tab-list.footer.2", Collections.singletonList("<#a4a832>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬</#d93316>"));
			settings.getRoot().set("tab-list.footer.3", Collections.singletonList("<#d93316>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬</#b02089>"));
			settings.getRoot().set("tab-list.footer.4", Collections.singletonList("<#b02089>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬</#8520b0>"));
			settings.getRoot().set("tab-list.footer.5", Collections.singletonList("<#8520b0>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬</#6134c9>"));
			settings.getRoot().set("tab-list.footer.6", Collections.singletonList("<#6134c9>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬</#3454c9>"));
			settings.getRoot().set("tab-list.footer.7", Collections.singletonList("<#3454c9>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬</#34b0c9>"));
			settings.getRoot().set("tab-list.footer.8", Collections.singletonList("<#34b0c9>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬</#33b8aa>"));
			settings.getRoot().set("tab-list.footer.9", Collections.singletonList("<#33b8aa>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬</#34e090>"));
			settings.getRoot().set("tab-list.footer.10", Collections.singletonList("<#32a8a6>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬</#35ba32>"));
			settings.getRoot().set("tab-list.footer.11", Collections.singletonList("<#34e090>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬</#9cde3a>"));
			settings.getRoot().set("tab-list.footer.12", Collections.singletonList("<#9cde3a>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬</#32a890>"));
			settings.getRoot().set("context.blacklist", Arrays.asList("nigger", "faggot"));
			settings.getRoot().set("context.with-held", Arrays.asList("ni", "n i", "n_i", "n1", "n 1", "n_1"));
			settings.getRoot().set("context.replacement", Arrays.asList("jake from statefarm", "dirty fart", "fish sandwich", "green eggs"));
			settings.getRoot().set("offense.fail-message", "&cSending messages too fast! Offense x {0}");
			settings.getRoot().set("offense.kick-message", "&cSpamming not allowed.");
			settings.getRoot().set("offense.max-before-kick", 5);
			settings.getRoot().set("context.send-cooldown", 3);
			settings.getRoot().set("messaging.out.normal", "&7[&b✉&7] &3&ome &r&l&m→&r &7{TARGET}");
			settings.getRoot().set("messaging.out.hover", " &7[&r{MESSAGE}&7]");
			settings.getRoot().set("messaging.out.meta", "&7Click to respond to the previous message.");
			settings.getRoot().set("messaging.in.normal", "&7[&b✉&7] &7{SENDER} &r&l&m→&r &3&ome");
			settings.getRoot().set("messaging.in.hover", " &7[&r{MESSAGE}&7]");
			settings.getRoot().set("messaging.in.meta", "&7Click to respond to my message.");
			settings.getRoot().set("motd.join", Arrays.asList("Welcome to our server {SERVER}", "It's nice to finally meet you %player_name%!"));
			settings.getRoot().set("motd.server-list", "My Server > Come play with us!");
			settings.getRoot().set("activity.join.enabled", true);
			settings.getRoot().set("activity.join.message", "{PLAYER} &7[&a+&7]");
			settings.getRoot().set("activity.leave.enabled", true);
			settings.getRoot().set("activity.leave.message", "{PLAYER} &7[&c-&7]");
			settings.getRoot().save();
			settings.getRoot().reload();
		}
		FileManager chats = MyChat.getChatsFile();
		if (chats.getRoot().getKeys(false).isEmpty()) {
			chats.getRoot().set("global.tag", "Global");
			chats.getRoot().set("global.node", "mess.chat.global");
			chats.getRoot().set("global.is-main", true);
			chats.getRoot().set("secret.tag", "Secret");
			chats.getRoot().set("secret.node", "mess.chat.secret");
			chats.getRoot().save();
		}
	}

	public static void generateSlot(String group) {
		FileManager format = MyChat.getFormatFile();
		if (!format.getRoot().isNode(group) || !format.getRoot().exists()) {
			format.getRoot().set(group + ".1.text", "&4&lX");
			format.getRoot().set(group + ".1.action", "chat delete {MESSAGE_ID}");
			format.getRoot().set(group + ".1.suggest", "");
			format.getRoot().set(group + ".1.url", "");
			format.getRoot().set(group + ".1.node", "this.permission.node");
			format.getRoot().set(group + ".1.prefix", "&7[");
			format.getRoot().set(group + ".1.suffix", "&7]");
			format.getRoot().set(group + ".1.hover", Collections.singletonList("&cClick to remove my message."));

			format.getRoot().set(group + ".2.text", "&e-");
			format.getRoot().set(group + ".2.action", "");
			format.getRoot().set(group + ".2.suggest", "");
			format.getRoot().set(group + ".2.url", "");
			format.getRoot().set(group + ".2.node", "this.permission.node");
			format.getRoot().set(group + ".2.prefix", "&7[");
			format.getRoot().set(group + ".2.suffix", "&7]");
			format.getRoot().set(group + ".2.hover", Collections.singletonList("&6Click to mute me"));

			format.getRoot().set(group + ".3.text", " &7%player_name%&r");
			format.getRoot().set(group + ".3.action", "");
			format.getRoot().set(group + ".3.suggest", "");
			format.getRoot().set(group + ".3.url", "");
			format.getRoot().set(group + ".3.node", "");
			format.getRoot().set(group + ".3.prefix", "");
			format.getRoot().set(group + ".3.suffix", "");
			format.getRoot().set(group + ".3.hover", new ArrayList<String>());

			format.getRoot().set(group + ".4.text", " &8»&r {MESSAGE}");
			format.getRoot().set(group + ".4.action", "");
			format.getRoot().set(group + ".4.suggest", "");
			format.getRoot().set(group + ".4.url", "");
			format.getRoot().set(group + ".4.node", "");
			format.getRoot().set(group + ".4.prefix", "");
			format.getRoot().set(group + ".4.suffix", "");
			format.getRoot().set(group + ".4.hover", new ArrayList<String>());
			format.getRoot().save();
		}
	}

}
