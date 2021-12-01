package com.github.sanctum.mychat.util;

import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.formatting.string.GradientColor;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.mychat.MyChat;
import com.github.sanctum.mychat.MySettings;
import com.github.sanctum.mychat.model.IChatComponentMeta;
import com.github.sanctum.myessentials.api.MyEssentialsAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

public class ChatComponentUtil {

	private static final List<GradientColor> COLORS = new ArrayList<>();
	private static final List<GradientColor> COLORS_WRAPPED = new ArrayList<>();

	/**
	 * @return Gets all currently configured groups by name.
	 */
	public static List<String> getFoundGroups() {
		return new ArrayList<>(MyChat.getFormatFile().getRoot().getKeys(false));
	}

	public static String randomReplacement() {
		return MySettings.WORD_REPLACEMENTS.get().get(Math.max(new Random().nextInt(MySettings.WORD_REPLACEMENTS.get().size() - 1), 0));
	}

	public static void loadColors() {
		COLORS.clear();
		COLORS_WRAPPED.clear();
		FileManager colors = MyEssentialsAPI.getInstance().getAddonFile("Colors", "Chat");
		if (colors.getRoot().exists()) {
			for (String key : colors.getRoot().getKeys(false)) {
				COLORS.add(new GradientColor(colors.getRoot().getString(key + ".from"), colors.getRoot().getString(key + ".to")).name("{" + colors.getRoot().getString(key + ".name").toUpperCase() + "}"));
				COLORS_WRAPPED.add(new GradientColor(colors.getRoot().getString(key + ".from"), colors.getRoot().getString(key + ".to")).name("{/" + colors.getRoot().getString(key + ".name").toUpperCase() + "}"));
			}
		}
	}

	public static String strip(String context) {
		for (CustomColor c : COLORS) {
			if (StringUtils.use(context).containsIgnoreCase(c.name())) {
				return StringUtils.use(context).replaceIgnoreCase(c.name(), "");
			}
		}
		for (CustomColor c : COLORS_WRAPPED) {
			if (StringUtils.use(context).containsIgnoreCase(c.name())) {
				return StringUtils.use(context).replaceIgnoreCase(c.name(), "");
			}
		}
		return context;
	}

	public static String wrap(String context) {
		for (GradientColor c : COLORS_WRAPPED) {
			if (StringUtils.use(context).containsIgnoreCase(c.name())) {
				return c.context(StringUtils.use(context).replaceIgnoreCase(c.name(), "")).translate();
			}
		}
		return context;
	}

	public static String motd(String context) {
		String[] args = context.split(" ", -1);
		for (int i = 0; i < args.length; i++) {
			for (GradientColor c : COLORS) {
				if (StringUtils.use(args[i]).containsIgnoreCase(c.name())) {
					args[i] = c.context(StringUtils.use(args[i]).replaceIgnoreCase(c.name(), "")).translate();
				}
			}
		}
		StringBuilder builder = new StringBuilder();
		for (String a : args) {
			builder.append(a).append(" ");
		}
		return builder.toString();
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

	public static String translate(String context) {
		String[] args = context.split(" ");
		for (int i = 0; i < args.length; i++) {
			for (GradientColor c : COLORS) {
				if (StringUtils.use(args[i]).containsIgnoreCase(c.name())) {
					args[i] = c.context(StringUtils.use(args[i]).replaceIgnoreCase(c.name(), "")).translate();
				}
			}
		}
		StringBuilder builder = new StringBuilder();
		for (String a : args) {
			builder.append(a).append(" ");
		}
		return builder.substring(0, builder.length() - 1);
	}

	public static String translate(Player source, String text) {
		return translate(StringUtils.use(text.replace("{clanspro_clan_name{player_name}}", "%clanspro_clan_name%")
				.replace("{CHAT_CHANNEL}", MyChat.getAddon().getLoader().getChannel(source).getChannel())
				.replace("{clanspro_clan_name}", "%clanspro_clan_name%")).translate(source));
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
						String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(strip(message)).translate());
						if (sender.hasPermission("mess.chat.color")) {
							hoverT = wrap(translate(MyChat.getAddon().getLoader().getChatColor(sender) + message));
						}
						TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", hoverT)).replace("{MESSAGE_ID}", "" + id)));
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
						String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(strip(message)).translate());
						if (sender.hasPermission("mess.chat.color")) {
							hoverT = wrap(translate(MyChat.getAddon().getLoader().getChatColor(sender) + message));
						}
						TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", hoverT)).replace("{MESSAGE_ID}", "" + id)));
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
						String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(strip(message)).translate());
						if (sender.hasPermission("mess.chat.color")) {
							hoverT = wrap(translate(MyChat.getAddon().getLoader().getChatColor(sender) + message));
						}
						TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", hoverT)).replace("{MESSAGE_ID}", "" + id)));
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
						String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(strip(message)).translate());
						if (sender.hasPermission("mess.chat.color")) {
							hoverT = wrap(translate(MyChat.getAddon().getLoader().getChatColor(sender) + message));
						}
						TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", hoverT)).replace("{MESSAGE_ID}", "" + id)));
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
					String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(strip(message)).translate());
					if (sender.hasPermission("mess.chat.color")) {
						hoverT = wrap(translate(MyChat.getAddon().getLoader().getChatColor(sender) + message));
					}
					TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", hoverT)).replace("{MESSAGE_ID}", "" + id)));
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
					String hoverT = org.bukkit.ChatColor.stripColor(StringUtils.use(strip(message)).translate());
					if (sender.hasPermission("mess.chat.color")) {
						hoverT = wrap(translate(MyChat.getAddon().getLoader().getChatColor(sender) + message));
					}
					TextComponent hover = new TextComponent(TextComponent.fromLegacyText(translate(sender, meta.getHoverText().replace("{MESSAGE}", hoverT)).replace("{MESSAGE_ID}", "" + id)));
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
