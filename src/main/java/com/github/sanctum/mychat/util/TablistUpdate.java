package com.github.sanctum.mychat.util;

import com.github.sanctum.labyrinth.formatting.TabGroup;
import com.github.sanctum.labyrinth.formatting.TabInfo;
import com.github.sanctum.labyrinth.formatting.TablistInstance;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.mychat.MyChat;
import com.github.sanctum.mychat.MySettings;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistUpdate {

	public static void load() {

		Bukkit.getOnlinePlayers().forEach(target -> {
			TablistInstance instance = TablistInstance.get(target);
			if (instance.isEnabled()) {
				instance.disable();
				if (instance.getGroup("SERVER") != null) {
					instance.remove(instance.getGroup("SERVER"));
				}
				instance.add(newServerTabGroup());
				Schedule.sync(() -> instance.enable(player -> player.setPlayerListName(ChatComponentUtil.translate(StringUtils.use(MySettings.TABLIST_PLAYER.getString().replace("{PLAYER}", player.getDisplayName())).translate(player))), TimeUnit.MILLISECONDS, MySettings.TABLIST_INTERVAL.getInt())).waitReal(40);

			} else {
				if (instance.getGroup("SERVER") != null) {
					instance.remove(instance.getGroup("SERVER"));
				}
				instance.add(newServerTabGroup());
				instance.enable(player -> player.setPlayerListName(ChatComponentUtil.translate(StringUtils.use(MySettings.TABLIST_PLAYER.getString().replace("{PLAYER}", player.getDisplayName())).translate(player))), TimeUnit.MILLISECONDS, MySettings.TABLIST_INTERVAL.getInt());
			}

		});
	}

	private static TabGroup newServerTabGroup() {
		return new TabGroup() {

			private final List<TabInfo> headerlist = Collections.synchronizedList(new LinkedList<>());
			private final List<TabInfo> footerlist = Collections.synchronizedList(new LinkedList<>());
			private int headerPos;
			private int footerPos;
			private boolean isActive = true;
			private boolean headerGoingBackwards;
			private boolean footerGoingBackwards;

			{
				for (Map.Entry<Integer, List<String>> entry : MyChat.getAddon().getLoader().HEADER_QUEUE.entrySet()) {
					TabInfo header = TabInfo.of();
					entry.getValue().forEach(header::put);
					headerlist.add(header);
				}
				for (Map.Entry<Integer, List<String>> entry : MyChat.getAddon().getLoader().FOOTER_QUEUE.entrySet()) {
					TabInfo footer = TabInfo.of();
					entry.getValue().forEach(footer::put);
					footerlist.add(footer);
				}
			}

			@Override
			public String getKey() {
				return "SERVER";
			}

			@Override
			public boolean isActive() {
				return isActive;
			}

			@Override
			public boolean isWindable() {
				return MySettings.TABLIST_WINDBACK.valid();
			}

			@Override
			public void setActive(boolean active) {
				this.isActive = active;
			}

			@Override
			public TabInfo getHeader(int index) {
				return Collections.unmodifiableList(headerlist).get(index);
			}

			@Override
			public int getCurrentHeaderIndex() {
				return headerPos;
			}

			@Override
			public TabInfo getFooter(int index) {
				return Collections.unmodifiableList(footerlist).get(index);
			}

			@Override
			public int getCurrentFooterIndex() {
				return footerPos;
			}

			@Override
			public void setWindable(boolean windable) {
				// default provision, let config control wind-ability.
			}

			@Override
			public void nextDisplayIndex(int side) {
				switch (side) {
					case 0:
						if (headerPos + 1 >= headerlist.size()) {
							if (isWindable()) {
								headerGoingBackwards = true;
								headerPos = headerPos - 1;
							} else {
								headerPos = 0;
							}
						} else {
							if (headerGoingBackwards) {
								if (headerPos == 0) {
									headerGoingBackwards = false;
									headerPos = headerPos + 1;
								} else {
									headerPos = headerPos - 1;
								}
							} else {
								headerPos = headerPos + 1;
							}
						}
						break;
					case 1:
						if (footerPos + 1 >= footerlist.size()) {
							if (isWindable()) {
								footerGoingBackwards = true;
								footerPos = footerPos - 1;
							} else {
								footerPos = 0;
							}
						} else {
							if (footerGoingBackwards) {
								if (footerPos == 0) {
									footerGoingBackwards = false;
									footerPos = footerPos + 1;
								} else {
									footerPos = footerPos - 1;
								}
							} else {
								footerPos = footerPos + 1;
							}
						}
						break;
				}
			}
		};
	}

	public static void to(final Player target) {
		TablistInstance tablist = TablistInstance.get(target);
		if (tablist.isEnabled()) tablist.disable();
		if (tablist.getGroup("SERVER") != null) {
			tablist.remove(tablist.getGroup("SERVER"));
		}
		tablist.add(newServerTabGroup());
		Schedule.sync(() -> tablist.enable(player -> player.setPlayerListName(ChatComponentUtil.translate(StringUtils.use(MySettings.TABLIST_PLAYER.getString().replace("{PLAYER}", player.getDisplayName())).translate(player))), TimeUnit.MILLISECONDS, MySettings.TABLIST_INTERVAL.getInt())).waitReal(2);
	}

}
