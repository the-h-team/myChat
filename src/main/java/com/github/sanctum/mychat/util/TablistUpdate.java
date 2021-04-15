package com.github.sanctum.mychat.util;

import com.github.sanctum.labyrinth.library.DirectivePoint;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.mychat.MyChat;
import com.github.sanctum.mychat.MySettings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public class TablistUpdate {

	private static final Map<Player, Integer> HEADER_INSERT = new HashMap<>();
	private static final Map<Player, Integer> FOOTER_INSERT = new HashMap<>();
	private static final Map<Player, Boolean> HEADER_DRAWNBACK = new HashMap<>();
	private static final Map<Player, Boolean> FOOTER_DRAWNBACK = new HashMap<>();

	private static void positionHeader(Player target, int i) {
		if (i == 24) {
			HEADER_INSERT.put(target, 0);
			return;
		}
		if (String.valueOf(i).contains("-")) {
			HEADER_DRAWNBACK.put(target, true);
		} else {
			HEADER_DRAWNBACK.put(target, false);
		}
		int result = header(target) + i;
		HEADER_INSERT.put(target, result);
	}

	private static void positionFooter(Player target, int i) {
		if (i == 24) {
			FOOTER_INSERT.put(target, 0);
			return;
		}
		if (String.valueOf(i).contains("-")) {
			FOOTER_DRAWNBACK.put(target, true);
		} else {
			FOOTER_DRAWNBACK.put(target, false);
		}
		int result = footer(target) + i;
		FOOTER_INSERT.put(target, result);
	}

	private static int header(Player target) {
		if (!HEADER_INSERT.containsKey(target)) {
			HEADER_INSERT.put(target, 0);
		}
		return HEADER_INSERT.get(target);
	}

	private static int footer(Player target) {
		if (!FOOTER_INSERT.containsKey(target)) {
			FOOTER_INSERT.put(target, 0);
		}
		return FOOTER_INSERT.get(target);
	}

	private static boolean headerDrawnback(Player target) {
		return HEADER_DRAWNBACK.getOrDefault(target, false);
	}

	private static boolean footerDrawnback(Player target) {
		return FOOTER_DRAWNBACK.getOrDefault(target, false);
	}

	public static void calculate(final Player target, TABSIDE side, List<TablistDisplay> list) {
		switch (side) {
			case TOP:
				if (header(target) + 1 >= list.size()) {
					if (MySettings.TABLIST_WINDBACK.valid()) {
						positionHeader(target, -1);
					} else {
						positionHeader(target, 24);
					}
				} else {
					if (headerDrawnback(target)) {
						if (header(target) == 0) {
							positionHeader(target, 1);
						} else {
							positionHeader(target, -1);
						}
					} else {
						positionHeader(target, 1);
					}
				}
				break;
			case BOTTOM:
				if (footer(target) + 1 >= list.size()) {
					if (MySettings.TABLIST_WINDBACK.valid()) {
						positionFooter(target, -1);
					} else {
						positionFooter(target, 24);
					}
				} else {
					if (footerDrawnback(target)) {
						if (footer(target) == 0) {
							positionFooter(target, 1);
						} else {
							positionFooter(target, -1);
						}
					} else {
						positionFooter(target, 1);
					}
				}
				break;
		}
	}

	public static void load() {
		MyChat.getAddon().getLoader().DISPLAY_HEADER.clear();
		for (Map.Entry<Integer, List<String>> entry : MyChat.getAddon().getLoader().HEADER.entrySet()) {
			TablistDisplay header = TablistDisplay.of();
			for (int i = 0; i < entry.getValue().size(); i++) {
				header.input(i, entry.getValue().get(i));
			}
			MyChat.getAddon().getLoader().DISPLAY_HEADER.add(header);
		}
		MyChat.getAddon().getLoader().DISPLAY_FOOTER.clear();
		for (Map.Entry<Integer, List<String>> entry : MyChat.getAddon().getLoader().FOOTER.entrySet()) {
			TablistDisplay footer = TablistDisplay.of();
			for (int i = 0; i < entry.getValue().size(); i++) {
				footer.input(i, entry.getValue().get(i));
			}
			MyChat.getAddon().getLoader().DISPLAY_FOOTER.add(footer);
		}
	}

	public static void to(final Player target) {
		Schedule.sync(() -> {
			List<TablistDisplay> HEADER = MyChat.getAddon().getLoader().DISPLAY_HEADER;
			calculate(target, TABSIDE.TOP, HEADER);
			List<TablistDisplay> FOOTER = MyChat.getAddon().getLoader().DISPLAY_FOOTER;
			calculate(target, TABSIDE.BOTTOM, FOOTER);
			target.setPlayerListHeaderFooter(
					ChatComponentUtil.translate(StringUtils.use(HEADER.get(Math.max(header(target), 0)).toString()
							.replace("{PLAYER_DIRECTION}", DirectivePoint.get(target).name())).translate(target)),
					ChatComponentUtil.translate(StringUtils.use(FOOTER.get(Math.max(footer(target), 0)).toString()
							.replace("{PLAYER_DIRECTION}", DirectivePoint.get(target).name())).translate(target)));
			target.setPlayerListName(ChatComponentUtil.translate(StringUtils.use(MySettings.TABLIST_PLAYER.getString().replace("{PLAYER}", target.getDisplayName())).translate(target)));
		}).debug().cancelAfter(task -> {
			if (MyChat.getAddon().getLoader().RELOADED) {
				task.cancel();
				return;
			}
			if (!target.isOnline()) {
				task.cancel();
			}
		}).repeatReal(0, MySettings.TABLIST_INTERVAL.getInt());
	}

	private enum TABSIDE {
		TOP, BOTTOM
	}

}
