package com.github.sanctum.mychat;

import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.mychat.model.AsyncChatChannel;
import com.github.sanctum.mychat.model.IChatComponentTrail;
import com.github.sanctum.mychat.model.IChatCooldownOffense;
import com.github.sanctum.mychat.util.ChatComponentUtil;
import com.github.sanctum.mychat.util.MyChatCooldown;
import com.github.sanctum.mychat.util.TablistUpdate;
import com.github.sanctum.myessentials.util.moderation.KickReason;
import com.github.sanctum.myessentials.util.moderation.PlayerSearch;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MyListener implements Listener {

	public void runTimerUpdates() {
		Schedule.async(() -> {
			for (Player p : Bukkit.getOnlinePlayers()) {
				Cooldown c = MyChat.getCooldown(p);
				if (c != null) {
					if (c.isComplete()) {
						Cooldown.remove(c);
						MyChat.getAddon().getLoader().OFFENSE_LOG.removeIf(o -> o.getOffender() == p);
						break;
					}
					if (c.getSecondsLeft() == 0) {
						Cooldown.remove(c);
						MyChat.getAddon().getLoader().OFFENSE_LOG.removeIf(o -> o.getOffender() == p);
						break;
					}
				}
			}
		}).repeat(0, 10);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (!e.isCancelled()) {
			if (!e.getPlayer().hasPermission("mess.chat.bypass")) {
				Cooldown c = MyChat.getCooldown(e.getPlayer());
				if (c != null) {
					if (c.isComplete()) {
						Cooldown.remove(c);
						MyChat.getAddon().getLoader().OFFENSE_LOG.removeIf(o -> o.getOffender() == e.getPlayer());
					}
					if (c.getSecondsLeft() == 0) {
						Cooldown.remove(c);
						MyChat.getAddon().getLoader().OFFENSE_LOG.removeIf(o -> o.getOffender() == e.getPlayer());
					}
					if (MyChat.getAddon().getLoader().getOffense(e.getPlayer()) != null) {
						IChatCooldownOffense offense = MyChat.getAddon().getLoader().getOffense(e.getPlayer());
						if (offense.getLevel() < MySettings.OFFENSE_MAX.getInt()) {
							offense.invokeLevel();
						} else {
							final PlayerSearch search = PlayerSearch.look(e.getPlayer());
							Schedule.sync(() -> search.kick(KickReason.next().input(1, MySettings.OFFENSE_KICK_MESSAGE.getString()).reason(MySettings.OFFENSE_KICK_MESSAGE.getString()), false)).run();
						}
					} else {
						MyChat.getAddon().getLoader().OFFENSE_LOG.add(new IChatCooldownOffense(e.getPlayer()));
					}
					e.getPlayer().sendMessage(StringUtils.use(MySettings.OFFENSE_MESSAGE.getString().replace("{0}", MyChat.getAddon().getLoader().getOffense(e.getPlayer()).getLevel() + "")).translate());
					if (c.getSecondsLeft() > 0) {
						e.getPlayer().sendMessage(StringUtils.use("&7[&4&l-&7] &7&oYou can use commands again in &c&l" + c.getSecondsLeft() + " &7&oseconds.").translate());
					}
					e.setCancelled(true);
				} else {
					MyChatCooldown cooldown = new MyChatCooldown(e.getPlayer().getUniqueId(), MySettings.COOLDOWN_TIME.getInt());
					cooldown.save();
				}
			}
			for (Player spy : MyChat.getAddon().getLoader().SPY_LOG) {
				LinkedList<BaseComponent> toSend = new LinkedList<>();
				toSend.addFirst(new TextComponent(StringUtils.use("&7[&5&lSpy&7] ").translate()));
				toSend.add(new TextComponent(StringUtils.use("&7" + e.getPlayer().getName() + " : &r&o" + e.getMessage()).translate()));
				BaseComponent[] result = toSend.toArray(new BaseComponent[toSend.size() - 1]);
				spy.spigot().sendMessage(result);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent e) {
		if (!e.isCancelled()) {
			String group = MyChat.getPermissionBase().getPrimaryGroup(e.getPlayer());
			final int key = MyChat.getAddon().loader.RESEND_MAP.size() + 1;
			if (MyChat.getAddon().getLoader().getChannel(e.getPlayer()) != null) {
				AsyncChatChannel channel = MyChat.getAddon().getLoader().getChannel(e.getPlayer());
				if (!channel.isMuted()) {
					if (!e.getPlayer().hasPermission("mess.chat.bypass")) {
						Cooldown c = MyChat.getCooldown(e.getPlayer());
						if (c != null) {
							if (c.isComplete()) {
								Cooldown.remove(c);
								MyChat.getAddon().getLoader().OFFENSE_LOG.removeIf(o -> o.getOffender() == e.getPlayer());
							}
							if (c.getSecondsLeft() == 0) {
								Cooldown.remove(c);
								MyChat.getAddon().getLoader().OFFENSE_LOG.removeIf(o -> o.getOffender() == e.getPlayer());
							}
							if (MyChat.getAddon().getLoader().getOffense(e.getPlayer()) != null) {
								IChatCooldownOffense offense = MyChat.getAddon().getLoader().getOffense(e.getPlayer());
								if (offense.getLevel() < MySettings.OFFENSE_MAX.getInt()) {
									offense.invokeLevel();
								} else {
									final PlayerSearch search = PlayerSearch.look(e.getPlayer());
									Schedule.sync(() -> search.kick(KickReason.next().input(1, MySettings.OFFENSE_KICK_MESSAGE.getString()).reason(MySettings.OFFENSE_KICK_MESSAGE.getString()), false)).run();
								}
							} else {
								MyChat.getAddon().getLoader().OFFENSE_LOG.add(new IChatCooldownOffense(e.getPlayer()));
							}
							e.getPlayer().sendMessage(StringUtils.use(MySettings.OFFENSE_MESSAGE.getString().replace("{0}", MyChat.getAddon().getLoader().getOffense(e.getPlayer()).getLevel() + "")).translate());
							if (c.getSecondsLeft() > 0) {
								e.getPlayer().sendMessage(StringUtils.use("&7[&4&l-&7] &7&oYou can chat again in &c&l" + c.getSecondsLeft() + " &7&oseconds.").translate());
							}
							e.getRecipients().clear();
							return;
						} else {
							MyChatCooldown cooldown = new MyChatCooldown(e.getPlayer().getUniqueId(), MySettings.COOLDOWN_TIME.getInt());
							cooldown.save();
						}
					}

					String msg = e.getMessage();
					for (String rep : MySettings.WORD_BLACKLIST.get()) {
						msg = StringUtils.use(msg).replaceIgnoreCase(rep, ChatComponentUtil.randomReplacement());
					}
					e.setMessage(msg);
					for (Player spy : MyChat.getAddon().getLoader().SPY_LOG) {
						LinkedList<BaseComponent> toSend = new LinkedList<>(ChatComponentUtil.getFormat(spy, e.getPlayer(), group, msg, key));
						toSend.addFirst(new TextComponent(StringUtils.use("&7[&5&lSpy&7] &7[&r" + channel.getChannel() + "&7] ").translate()));
						BaseComponent[] result = toSend.toArray(new BaseComponent[toSend.size() - 1]);
						IChatComponentTrail resendable = new IChatComponentTrail(e.getPlayer(), channel, e.getMessage(), key);
						MyChat.getAddon().loader.RESEND_MAP.put(key, resendable);
						spy.spigot().sendMessage(result);
					}

					for (Player recipient : channel.getUsers()) {
						if (!MyChat.getAddon().getLoader().SPY_LOG.contains(recipient)) {
							List<BaseComponent> toSend = ChatComponentUtil.getFormat(recipient, e.getPlayer(), group, msg, key);
							BaseComponent[] result = toSend.toArray(new BaseComponent[toSend.size() - 1]);
							IChatComponentTrail resendable = new IChatComponentTrail(e.getPlayer(), channel, e.getMessage(), key);
							MyChat.getAddon().loader.RESEND_MAP.put(key, resendable);
							recipient.spigot().sendMessage(result);
						}
					}
				} else {
					Message.form(e.getPlayer()).send("&c&oThis chat channel is currently muted, unable to speak.");
				}
			}
			e.getRecipients().clear();
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (MyChat.getAddon().getLoader().getChatChannels().stream().noneMatch(ch -> ch.getUsers().contains(e.getPlayer()))) {
			for (AsyncChatChannel channel : MyChat.getAddon().getLoader().getChatChannels()) {
				if (channel.isDefault()) {
					channel.addUser(e.getPlayer());
					if (MySettings.TABLIST_USED.valid()) {
						TablistUpdate.to(e.getPlayer());
					}
					break;
				}
			}
		}
	}

}
