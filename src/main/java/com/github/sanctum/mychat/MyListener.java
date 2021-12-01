package com.github.sanctum.mychat;

import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import com.github.sanctum.labyrinth.interfacing.OrdinalProcedure;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TextLib;
import com.github.sanctum.labyrinth.permissions.Permissions;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.mychat.model.IChatChannel;
import com.github.sanctum.mychat.model.IChatComponentTrail;
import com.github.sanctum.mychat.model.IChatCooldownOffense;
import com.github.sanctum.mychat.util.ChatComponentUtil;
import com.github.sanctum.mychat.util.MyChatCooldown;
import com.github.sanctum.mychat.util.MyChatMute;
import com.github.sanctum.mychat.util.TablistUpdate;
import com.github.sanctum.myessentials.api.MyEssentialsAPI;
import com.github.sanctum.myessentials.util.moderation.KickReason;
import com.github.sanctum.myessentials.util.moderation.PlayerSearch;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class MyListener implements Listener {

	public void runTimerUpdates() {
		Schedule.async(() -> {
			for (Player p : Bukkit.getOnlinePlayers()) {
				Cooldown c = MyChat.getCooldown(p);
				if (c != null) {
					if (c.isComplete() || c.getSecondsLeft() == 0) {
						Cooldown.remove(c);
						MyChat.getAddon().getLoader().removeOffense(p);
						break;
					}
				}
				Cooldown mute = MyChatMute.get(p);
				if (mute != null) {
					if (mute.isComplete() || mute.getSecondsLeft() == 0) {
						if (MyChatMute.muted(p)) {
							MyChatMute.unmute(p);
						} else {
							Cooldown.remove(mute);
						}
						Message.form(p).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&2You are no longer muted.");
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
						MyChat.getAddon().getLoader().removeOffense(e.getPlayer());
					}
					if (c.getSecondsLeft() == 0) {
						Cooldown.remove(c);
						MyChat.getAddon().getLoader().removeOffense(e.getPlayer());
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
						MyChat.getAddon().getLoader().newOffense(e.getPlayer());
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
			for (Player spy : MyChat.getAddon().getLoader().getSpies()) {
				LinkedList<BaseComponent> toSend = new LinkedList<>();
				toSend.addFirst(new TextComponent(StringUtils.use("&7[&5&lSpy&7] ").translate()));
				toSend.add(new TextComponent(StringUtils.use("&7" + e.getPlayer().getName() + " : &r&o" + e.getMessage()).translate()));
				BaseComponent[] result = toSend.toArray(new BaseComponent[toSend.size() - 1]);
				spy.spigot().sendMessage(result);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (!e.isCancelled()) {
			Permissions permissions = MyChat.getAddon().getPermissions();
			String group = permissions.getUser(e.getPlayer()).getGroup().getName();
			int key = MyChat.getAddon().getLoader().getTrails().size() + 1;
			if (MyChat.getAddon().getLoader().getChannel(e.getPlayer()) != null) {
				IChatChannel channel = MyChat.getAddon().getLoader().getChannel(e.getPlayer());
				if (MyChatMute.muted(e.getPlayer())) {
					if (MyChatMute.get(e.getPlayer()) != null) {
						Message.form(e.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&4&oYou are muted, you can speak again in " + MyChatMute.get(e.getPlayer()).fullTimeLeft());
					} else {
						Message.form(e.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&4&oYou are muted, unable to speak.");
					}
					e.setMessage("{MUTED MUMBO JUMBO}");
					e.getRecipients().clear();
					return;
				}
				if (!channel.isMuted()) {
					if (!e.getPlayer().hasPermission("mess.chat.bypass")) {
						Cooldown c = MyChat.getCooldown(e.getPlayer());
						if (c != null) {
							if (c.isComplete()) {
								Cooldown.remove(c);
								MyChat.getAddon().getLoader().removeOffense(e.getPlayer());
							}
							if (c.getSecondsLeft() == 0) {
								Cooldown.remove(c);
								MyChat.getAddon().getLoader().removeOffense(e.getPlayer());
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
								MyChat.getAddon().getLoader().newOffense(e.getPlayer());
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
					for (String regex : MySettings.WORD_BLACKLIST.get()) {
						msg = StringUtils.use(msg).replaceIgnoreCase(regex, ChatComponentUtil.randomReplacement());
					}
					e.setMessage(msg);
					IChatComponentTrail resendable = new IChatComponentTrail(e.getPlayer(), channel, e.getMessage(), key);
					for (Player spy : MyChat.getAddon().getLoader().getSpies()) {
						LinkedList<BaseComponent> toSend = new LinkedList<>(ChatComponentUtil.getFormat(spy, e.getPlayer(), group, msg, key));
						toSend.addFirst(new TextComponent(StringUtils.use("&7[&5&lSpy&7] &7[&r" + channel.getChannel() + "&7] ").translate()));
						BaseComponent[] result = toSend.toArray(new BaseComponent[toSend.size() - 1]);
						OrdinalProcedure.select(MyChat.getAddon().getLoader(), 0, key, resendable);
						spy.spigot().sendMessage(result);
					}

					for (Player online : Bukkit.getOnlinePlayers()) {
						if (online.hasPermission(channel.getPermission()) && !channel.getUsers().contains(online)) {
							Message.form(online).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&aChannel " + channel.getChannel() + " just got a new message.");
						}
					}

					if (MySettings.WORD_WITHHOLD_LIST.get().stream().anyMatch(s -> StringUtils.use(e.getMessage()).containsIgnoreCase(s))) {
						key = MyChat.getAddon().getLoader().getWitheld().size() + 1;
						LinkedList<BaseComponent> toSend = new LinkedList<>();
						OrdinalProcedure.select(MyChat.getAddon().getLoader(), 5, key, resendable);
						for (Player online : Bukkit.getOnlinePlayers()) {
							if (online.hasPermission("mess.chat.message.moderate")) {
								toSend.addFirst(TextLib.getInstance().textRunnable(MyEssentialsAPI.getInstance().getPrefix() + " " + e.getPlayer().getName() + " &ais attempting to send a message that needs your approval. ", "\n&7[&a&l√&7]", " &7| ", "&7[&c&lX&7] &r&l&m→&r ", "&bClick to approve my message.", "&cClick to deny my message", "chat approve " + key, "chat deny " + key));
								toSend.add(1, new ColoredString("&r" + '"' + "&e" + ChatComponentUtil.translate(e.getMessage()) + "&r" + '"', ColoredString.ColorType.MC_COMPONENT).toComponent());
								BaseComponent[] with = toSend.toArray(new BaseComponent[toSend.size() - 1]);
								online.spigot().sendMessage(with);
							}
						}
						Message.form(e.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&c&oYour message contains possible profanity that a moderator needs to approve before being sent.");
						e.getRecipients().clear();
						return;
					}

					for (Player recipient : channel.getUsers()) {
						if (!MyChat.getAddon().getLoader().isSpying(recipient)) {
							LinkedList<BaseComponent> toSend = new LinkedList<>(ChatComponentUtil.getFormat(recipient, e.getPlayer(), group, msg, key));
							BaseComponent[] result = toSend.toArray(new BaseComponent[toSend.size() - 1]);
							OrdinalProcedure.select(MyChat.getAddon().getLoader(), 0, key, resendable);
							recipient.spigot().sendMessage(result);
						}
					}
				} else {
					Message.form(e.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&c&oThis chat channel is currently muted, unable to speak.");
				}
			}
			e.getRecipients().clear();
		}
	}

	@EventHandler
	public void onPing(ServerListPingEvent e) {
		e.setMotd(ChatComponentUtil.wrap(ChatComponentUtil.translate(StringUtils.use(MySettings.MOTD_SERVER.getString()).translate())).replace("{NEXT}", "\n"));
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (MySettings.LEAVE.valid()) {
			e.setQuitMessage(ChatComponentUtil.wrap(ChatComponentUtil.translate(StringUtils.use(MySettings.LEAVE.getString().replace("{PLAYER}", e.getPlayer().getName())).translate())));
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (MySettings.JOIN.valid()) {
			e.setJoinMessage(ChatComponentUtil.wrap(ChatComponentUtil.translate(StringUtils.use(MySettings.JOIN.getString().replace("{PLAYER}", e.getPlayer().getName())).translate())));
		}
		Message msg = Message.form(e.getPlayer());
		for (String message : MySettings.MOTD_JOIN.get()) {
			msg.send(ChatComponentUtil.motd(ChatComponentUtil.wrap(StringUtils.use(message).papi(e.getPlayer()))));
		}
		if (MyChat.getAddon().getLoader().getChatChannels().stream().noneMatch(ch -> ch.getUsers().contains(e.getPlayer()))) {
			for (IChatChannel channel : MyChat.getAddon().getLoader().getChatChannels()) {
				if (channel.isDefault()) {
					channel.addUser(e.getPlayer());
					break;
				}
			}
		}

		if (MySettings.TABLIST_USED.valid()) {
			TablistUpdate.to(e.getPlayer());
		}

		FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
		if (!user.getConfig().isConfigurationSection(e.getPlayer().getUniqueId() + ".mail")) {
			user.getConfig().createSection(e.getPlayer().getUniqueId() + ".mail");
			user.saveConfig();
			user.reload();
		}
		Set<String> mailbox = user.getConfig().getConfigurationSection(e.getPlayer().getUniqueId() + ".mail").getKeys(false);
		Map<OfflinePlayer, Integer> mailCount = new HashMap<>();
		for (String id : mailbox) {
			OfflinePlayer sender = Bukkit.getOfflinePlayer(UUID.fromString(id));
			mailCount.putIfAbsent(sender, 0);
			List<String> mail = user.getConfig().getStringList(e.getPlayer().getUniqueId() + ".mail." + id);
			mailCount.put(sender, mail.size());
		}
		mailCount.forEach((key, value) -> {
			if (value != 0)
				Message.form(e.getPlayer()).build(TextLib.getInstance().textSuggestable("&6&oPlayer &e" + key.getName() + " &6&osent you &a&l" + value + " &6&onew mail.", " &7[&bRespond&7]", "&7Click to view & respond to the mail.", "mail " + key.getName()));
		});
	}

}
