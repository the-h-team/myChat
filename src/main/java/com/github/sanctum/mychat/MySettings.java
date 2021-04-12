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

	WORD_BLACKLIST, WORD_REPLACEMENTS, TABLIST_USED, TABLIST_WINDBACK, TABLIST_HEADER, TABLIST_FOOTER, TABLIST_INTERVAL, TABLIST_PLAYER, COOLDOWN_TIME, OFFENSE_MAX, OFFENSE_MESSAGE, OFFENSE_KICK_MESSAGE;

	private static final FileManager settings = MyEssentialsAPI.getInstance().getAddonFile("Settings", "Chat");

	public Map<Integer, List<String>> grab() {
		Map<Integer, List<String>> result = new HashMap<>();
		switch (this) {
			case TABLIST_HEADER:
				for (String i : settings.getConfig().getConfigurationSection("tab-list.header").getKeys(false)) {
					result.put(Integer.parseInt(i), settings.getConfig().getStringList("tab-list.header." + i));
				}
				return result;
			case TABLIST_FOOTER:
				for (String i : settings.getConfig().getConfigurationSection("tab-list.footer").getKeys(false)) {
					result.put(Integer.parseInt(i), settings.getConfig().getStringList("tab-list.footer." + i));
				}
				return result;
		}
		return new HashMap<>();
	}

	public List<String> get() {
		switch (this) {
			case WORD_BLACKLIST:
				return settings.getConfig().getStringList("context.blacklist");
			case WORD_REPLACEMENTS:
				return settings.getConfig().getStringList("context.replacement");
		}
		return new ArrayList<>();
	}

	public int getInt() {
		switch (this) {
			case COOLDOWN_TIME:
				return settings.getConfig().getInt("context.send-cooldown");
			case OFFENSE_MAX:
				return settings.getConfig().getInt("offense.max-before-kick");
			case TABLIST_INTERVAL:
				return settings.getConfig().getInt("tab-list.update-interval");
		}
		return 0;
	}

	public String getString() {
		switch (this) {
			case OFFENSE_MESSAGE:
				return settings.getConfig().getString("offense.fail-message");
			case OFFENSE_KICK_MESSAGE:
				return settings.getConfig().getString("offense.kick-message");
			case TABLIST_PLAYER:
				return settings.getConfig().getString("tab-list.player-name");
		}
		return "";
	}

	public boolean valid() {
		switch (this) {
			case TABLIST_USED:
				return settings.getConfig().getBoolean("tab-list.enabled");
			case TABLIST_WINDBACK:
				if (!settings.exists()) {
					return true;
				}
				return settings.getConfig().getBoolean("tab-list.wind-back");
		}
		return false;
	}

	public static FileManager getSettings() {
		return settings;
	}

	public static void loadDefaults() {
		if (!MySettings.getSettings().exists()) {
			FileManager settings = MySettings.getSettings();
			settings.getConfig().set("tab-list.enabled", true);
			settings.getConfig().set("tab-list.wind-back", true);
			settings.getConfig().set("tab-list.update-interval", 1);
			settings.getConfig().set("tab-list.player-name", "{PLAYER}");
			settings.getConfig().set("tab-list.header.1", Arrays.asList("<#32a852>My Server</#8532a8>", "<#32a8a6>▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#a4a832>"));
			settings.getConfig().set("tab-list.header.2", Arrays.asList("<#32a852>My Server</#8532a8>", "<#a4a832>▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#d93316>"));
			settings.getConfig().set("tab-list.header.3", Arrays.asList("<#32a852>My Server</#8532a8>", "<#d93316>▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#b02089>"));
			settings.getConfig().set("tab-list.header.4", Arrays.asList("<#32a852>My Server</#8532a8>", "<#b02089>▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#8520b0>"));
			settings.getConfig().set("tab-list.header.5", Arrays.asList("<#32a852>My Server</#8532a8>", "<#8520b0>▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#6134c9>"));
			settings.getConfig().set("tab-list.header.6", Arrays.asList("<#32a852>My Server</#8532a8>", "<#6134c9>▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#3454c9>"));
			settings.getConfig().set("tab-list.header.7", Arrays.asList("<#32a852>My Server</#8532a8>", "<#3454c9>▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#34b0c9>"));
			settings.getConfig().set("tab-list.header.8", Arrays.asList("<#32a852>My Server</#8532a8>", "<#34b0c9>▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#33b8aa>"));
			settings.getConfig().set("tab-list.header.9", Arrays.asList("<#32a852>My Server</#8532a8>", "<#33b8aa>▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#34e090>"));
			settings.getConfig().set("tab-list.header.10", Arrays.asList("<#32a852>My Server</#8532a8>", "<#34e090>▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#35ba32>"));
			settings.getConfig().set("tab-list.header.11", Arrays.asList("<#32a852>My Server</#8532a8>", "<#35ba32>▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#9cde3a>"));
			settings.getConfig().set("tab-list.header.12", Arrays.asList("<#32a852>My Server</#8532a8>", "<#9cde3a>▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</#32a890>"));
			settings.getConfig().set("tab-list.footer.1", Collections.singletonList("<#32a8a6>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬▬</#a4a832>"));
			settings.getConfig().set("tab-list.footer.2", Collections.singletonList("<#a4a832>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬▬</#d93316>"));
			settings.getConfig().set("tab-list.footer.3", Collections.singletonList("<#d93316>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬▬</#b02089>"));
			settings.getConfig().set("tab-list.footer.4", Collections.singletonList("<#b02089>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬▬</#8520b0>"));
			settings.getConfig().set("tab-list.footer.5", Collections.singletonList("<#8520b0>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬▬</#6134c9>"));
			settings.getConfig().set("tab-list.footer.6", Collections.singletonList("<#6134c9>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬▬</#3454c9>"));
			settings.getConfig().set("tab-list.footer.7", Collections.singletonList("<#3454c9>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬▬</#34b0c9>"));
			settings.getConfig().set("tab-list.footer.8", Collections.singletonList("<#34b0c9>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬▬</#33b8aa>"));
			settings.getConfig().set("tab-list.footer.9", Collections.singletonList("<#33b8aa>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬</#34e090>"));
			settings.getConfig().set("tab-list.footer.10", Collections.singletonList("<#32a8a6>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬▬</#35ba32>"));
			settings.getConfig().set("tab-list.footer.11", Collections.singletonList("<#34e090>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬▬</#9cde3a>"));
			settings.getConfig().set("tab-list.footer.12", Collections.singletonList("<#9cde3a>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬o▬</#32a890>"));
			settings.getConfig().set("context.blacklist", Arrays.asList("nigger", "faggot"));
			settings.getConfig().set("context.replacement", Arrays.asList("jake from statefarm", "dirty fart", "fish sandwich", "green eggs"));
			settings.getConfig().set("offense.fail-message", "&cSending messages too fast! Offense x {0}");
			settings.getConfig().set("offense.kick-message", "&cSpamming not allowed.");
			settings.getConfig().set("offense.max-before-kick", 5);
			settings.getConfig().set("context.send-cooldown", 3);
			settings.saveConfig();
			settings.reload();
		}
		FileManager chats = MyChat.getChatsFile();
		if (chats.getConfig().getKeys(false).isEmpty()) {
			chats.getConfig().set("global.tag", "Global");
			chats.getConfig().set("global.node", "mess.chat.global");
			chats.getConfig().set("global.is-main", true);
			chats.getConfig().set("secret.tag", "Secret");
			chats.getConfig().set("secret.node", "mess.chat.secret");
			chats.saveConfig();
		}
	}

	public static void generateSlot(String group) {
		FileManager format = MyChat.getFormatFile();
		if (!format.getConfig().isConfigurationSection(group)) {
			format.getConfig().set(group + ".1.text", "&4&lX");
			format.getConfig().set(group + ".1.action", "chat delete {MESSAGE_ID}");
			format.getConfig().set(group + ".1.suggest", "");
			format.getConfig().set(group + ".1.url", "");
			format.getConfig().set(group + ".1.node", "this.permission.node");
			format.getConfig().set(group + ".1.prefix", "&7[");
			format.getConfig().set(group + ".1.suffix", "&7]");
			format.getConfig().set(group + ".1.hover", Collections.singletonList("&cClick to remove my message."));

			format.getConfig().set(group + ".2.text", "&e-");
			format.getConfig().set(group + ".2.action", "");
			format.getConfig().set(group + ".2.suggest", "");
			format.getConfig().set(group + ".2.url", "");
			format.getConfig().set(group + ".2.node", "this.permission.node");
			format.getConfig().set(group + ".2.prefix", "&7[");
			format.getConfig().set(group + ".2.suffix", "&7]");
			format.getConfig().set(group + ".2.hover", Collections.singletonList("&6Click to mute me"));

			format.getConfig().set(group + ".3.text", " &7%player_name%&r");
			format.getConfig().set(group + ".3.action", "");
			format.getConfig().set(group + ".3.suggest", "");
			format.getConfig().set(group + ".3.url", "");
			format.getConfig().set(group + ".3.node", "");
			format.getConfig().set(group + ".3.prefix", "");
			format.getConfig().set(group + ".3.suffix", "");
			format.getConfig().set(group + ".3.hover", new ArrayList<String>());

			format.getConfig().set(group + ".4.text", " &8»&r {MESSAGE}");
			format.getConfig().set(group + ".4.action", "");
			format.getConfig().set(group + ".4.suggest", "");
			format.getConfig().set(group + ".4.url", "");
			format.getConfig().set(group + ".4.node", "");
			format.getConfig().set(group + ".4.prefix", "");
			format.getConfig().set(group + ".4.suffix", "");
			format.getConfig().set(group + ".4.hover", new ArrayList<String>());
			format.saveConfig();
		}
	}

}
