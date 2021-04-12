package com.github.sanctum.mychat.util;

import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.mychat.MyChat;
import com.github.sanctum.mychat.MySettings;
import com.github.sanctum.mychat.model.IChatComponentMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

public class ChatComponentUtil {

	/**
	 * @return Gets all currently configured groups by name.
	 */
	public static List<String> getFoundGroups() {
		return new ArrayList<>(MyChat.getFormatFile().getConfig().getKeys(false));
	}

	public static String randomReplacement() {
		return MySettings.WORD_REPLACEMENTS.get().get(Math.max(new Random().nextInt(MySettings.WORD_REPLACEMENTS.get().size() - 1), 0));
	}

	/**
	 * @return The fallback group for the instance of an absent group in file.
	 */
	public static String getFallback() {
		String def = "Default";
		for (String group : getFoundGroups()) {
			if (MyChat.getFormatFile().getConfig().getBoolean(group + ".fallback")) {
				def = group;
				break;
			}
		}
		return def;
	}

	public static String translate(Player source, String text) {
		if (PlaceholderAPI.setPlaceholders(source, "%clanspro_clan_name%").isEmpty()) {
			return StringUtils.use(StringUtils.use(text.replace("{clanspro_clan_name{player_name}}", "%player_name%")
					.replace("{clanspro_clan_name}", "&cNot in one.&r")
					.replace("{CHAT_CHANNEL}", MyChat.getAddon().getLoader().getChannel(source).getChannel())).papi(source)).translate();
		}
		return StringUtils.use(StringUtils.use(text.replace("{clanspro_clan_name{player_name}}", "%clanspro_clan_name%")
				.replace("{CHAT_CHANNEL}", MyChat.getAddon().getLoader().getChannel(source).getChannel())
				.replace("{clanspro_clan_name}", "%clanspro_clan_name%")).papi(source)).translate();
	}

	public static List<BaseComponent> getFormat(Player viewer, Player sender, String group, String message, int id) {
		List<BaseComponent> components = new ArrayList<>();
		for (IChatComponentMeta meta : MyChat.getAddon().getLoader().getMeta(group)) {
			if (meta.getPermission() != null && !meta.getPermission().isEmpty()) {
				if (viewer.hasPermission(meta.getPermission()) && viewer != sender) {
					TextComponent base;
					String prefix = "";
					String suffix = "";
					if (meta.getPrefix() != null) {
						prefix = meta.getPrefix();
					}
					if (meta.getSuffix() != null) {
						suffix = meta.getSuffix();
					}
					if (meta.getAction() != null) {
						TextComponent text = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(prefix).translate()));
						String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(message).translate());
						if (sender.hasPermission("mess.chat.color")) {
							hoverT = message;
						}
						TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", MyChat.getAddon().getLoader().getChatColor(sender) + hoverT)).replace("{MESSAGE_ID}", "" + id)));
						TextComponent text2 = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(suffix).translate()));
						text.addExtra(hover);
						text.addExtra(text2);
						if (!meta.getAction().isEmpty()) {
							hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + translate(sender, meta.getAction().replace("{MESSAGE_ID}", "" + id))));
						}
						if (meta.getSuggestion() != null) {
							if (!meta.getSuggestion().isEmpty()) {
								hover.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, translate(sender, meta.getSuggestion())));
							}
						}
						if (meta.getUrl() != null) {
							if (!meta.getUrl().isEmpty()) {
								hover.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, translate(sender, meta.getUrl())));
							}
						}
						List<Content> array = new ArrayList<>();
						for (String msg : meta.getHoverMeta()) {
							array.add(new Text(TextComponent.fromLegacyText(translate(sender, msg))));
						}
						hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, array));
						base = text;
					} else {
						TextComponent text = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(prefix).translate()));
						String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(message).translate());;
						if (sender.hasPermission("mess.chat.color")) {
							hoverT = message;
						}
						TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", MyChat.getAddon().getLoader().getChatColor(sender) + hoverT)).replace("{MESSAGE_ID}", "" + id)));
						if (meta.getSuggestion() != null) {
							if (!meta.getSuggestion().isEmpty()) {
								hover.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, translate(sender, meta.getSuggestion())));
							}
						}
						if (meta.getUrl() != null) {
							if (!meta.getUrl().isEmpty()) {
								hover.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, translate(sender, meta.getUrl())));
							}
						}
						TextComponent text2 = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(suffix).translate()));
						text.addExtra(hover);
						text.addExtra(text2);
						List<Content> array = new ArrayList<>();
						for (String msg : meta.getHoverMeta()) {
							array.add(new Text(TextComponent.fromLegacyText(translate(sender, msg))));
						}
						hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, array));
						base = text;
					}

					components.add(base);
				}
				if (viewer.hasPermission(meta.getPermission()) && viewer == sender) {
					TextComponent base;
					String prefix = "";
					String suffix = "";
					if (meta.getPrefix() != null) {
						prefix = meta.getPrefix();
					}
					if (meta.getSuffix() != null) {
						suffix = meta.getSuffix();
					}
					if (meta.getAction() != null) {
						TextComponent text = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(prefix).translate()));
						String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(message).translate());
						if (sender.hasPermission("mess.chat.color")) {
							hoverT = message;
						}
						TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", MyChat.getAddon().getLoader().getChatColor(sender) + hoverT)).replace("{MESSAGE_ID}", "" + id)));
						TextComponent text2 =new TextComponent(TextComponent.fromLegacyText(StringUtils.use(suffix).translate()));
						text.addExtra(hover);
						text.addExtra(text2);
						if (!meta.getAction().isEmpty()) {
							hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + translate(sender, meta.getAction().replace("{MESSAGE_ID}", "" + id))));
						}
						if (meta.getSuggestion() != null) {
							if (!meta.getSuggestion().isEmpty()) {
								hover.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, translate(sender, meta.getSuggestion())));
							}
						}
						if (meta.getUrl() != null) {
							if (!meta.getUrl().isEmpty()) {
								hover.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, translate(sender, meta.getUrl())));
							}
						}
						List<Content> array = new ArrayList<>();
						for (String msg : meta.getHoverMeta()) {
							array.add(new Text(TextComponent.fromLegacyText(translate(sender, msg))));
						}
						hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, array));
						base = text;
					} else {
						TextComponent text = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(prefix).translate()));
						String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(message).translate());;
						if (sender.hasPermission("mess.chat.color")) {
							hoverT = message;
						}
						TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", MyChat.getAddon().getLoader().getChatColor(sender) + hoverT)).replace("{MESSAGE_ID}", "" + id)));
						TextComponent text2 = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(suffix).translate()));
						text.addExtra(hover);
						text.addExtra(text2);
						if (meta.getSuggestion() != null) {
							if (!meta.getSuggestion().isEmpty()) {
								hover.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, translate(sender, meta.getSuggestion())));
							}
						}
						if (meta.getUrl() != null) {
							if (!meta.getUrl().isEmpty()) {
								hover.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, translate(sender, meta.getUrl())));
							}
						}
						List<Content> array = new ArrayList<>();
						for (String msg : meta.getHoverMeta()) {
							array.add(new Text(TextComponent.fromLegacyText(translate(sender, msg))));
						}
						hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, array));
						base = text;
					}

					components.add(base);
				}

			} else {
				TextComponent base;
				String prefix = "";
				String suffix = "";
				if (meta.getPrefix() != null) {
					prefix = meta.getPrefix();
				}
				if (meta.getSuffix() != null) {
					suffix = meta.getSuffix();
				}
				if (meta.getAction() != null) {
					TextComponent text = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(prefix).translate()));
					String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(message).translate());;
					if (sender.hasPermission("mess.chat.color")) {
						hoverT = message;
					}
					TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", MyChat.getAddon().getLoader().getChatColor(sender) + hoverT)).replace("{MESSAGE_ID}", "" + id)));
					TextComponent text2 = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(suffix).translate()));
					text.addExtra(hover);
					text.addExtra(text2);
					if (!meta.getAction().isEmpty()) {
						hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + translate(sender, meta.getAction().replace("{MESSAGE_ID}", "" + id))));
					}
					if (meta.getSuggestion() != null) {
						if (!meta.getSuggestion().isEmpty()) {
							hover.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, translate(sender, meta.getSuggestion())));
						}
					}
					if (meta.getUrl() != null) {
						if (!meta.getUrl().isEmpty()) {
							hover.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, translate(sender, meta.getUrl())));
						}
					}
					List<Content> array = new ArrayList<>();
					for (String msg : meta.getHoverMeta()) {
						array.add(new Text(TextComponent.fromLegacyText(translate(sender, msg))));
					}
					hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, array));
					base = text;
				} else {
					TextComponent text = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(prefix).translate()));
					String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(message).translate());;
					if (sender.hasPermission("mess.chat.color")) {
						hoverT = message;
					}
					TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", MyChat.getAddon().getLoader().getChatColor(sender) + hoverT)).replace("{MESSAGE_ID}", "" + id)));
					TextComponent text2 = new TextComponent(TextComponent.fromLegacyText(StringUtils.use(suffix).translate()));
					text.addExtra(hover);
					text.addExtra(text2);
					if (meta.getSuggestion() != null) {
						if (!meta.getSuggestion().isEmpty()) {
							hover.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, translate(sender, meta.getSuggestion())));
						}
					}
					if (meta.getUrl() != null) {
						if (!meta.getUrl().isEmpty()) {
							hover.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, translate(sender, meta.getUrl())));
						}
					}
					List<Content> array = new ArrayList<>();
					for (String msg : meta.getHoverMeta()) {
						array.add(new Text(TextComponent.fromLegacyText(translate(sender, msg))));
					}
					hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, array));
					base = text;
				}
				components.add(base);
			}
		}
		return components;
	}

}
