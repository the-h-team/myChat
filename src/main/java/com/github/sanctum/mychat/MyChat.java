package com.github.sanctum.mychat;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.formatting.PaginatedList;
import com.github.sanctum.labyrinth.formatting.completion.SimpleTabCompletion;
import com.github.sanctum.labyrinth.formatting.completion.TabCompletionIndex;
import com.github.sanctum.labyrinth.interfacing.OrdinalProcedure;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.TextLib;
import com.github.sanctum.labyrinth.permissions.Permissions;
import com.github.sanctum.labyrinth.permissions.entity.Group;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.mychat.model.IChatChannel;
import com.github.sanctum.mychat.model.IChatComponentMeta;
import com.github.sanctum.mychat.model.IChatComponentTrail;
import com.github.sanctum.mychat.model.IChatMetaLoader;
import com.github.sanctum.mychat.util.ChatComponentUtil;
import com.github.sanctum.mychat.util.MyChatMute;
import com.github.sanctum.mychat.util.TablistUpdate;
import com.github.sanctum.myessentials.Essentials;
import com.github.sanctum.myessentials.api.EssentialsAddon;
import com.github.sanctum.myessentials.api.MyEssentialsAPI;
import com.github.sanctum.myessentials.model.CommandBuilder;
import com.github.sanctum.myessentials.model.CommandMapper;
import com.github.sanctum.myessentials.util.moderation.PlayerSearch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MyChat extends EssentialsAddon {

	private IChatMetaLoader loader;
	private SimpleTabCompletion chatTab;
	private SimpleTabCompletion messageTab;
	private SimpleTabCompletion replyTab;
	private static final FileManager FILE = MyEssentialsAPI.getInstance().getAddonFile("Format", "Chat");
	private static final FileManager CHATS = MyEssentialsAPI.getInstance().getAddonFile("Channels", "Chat");
	private static MyChat instance;


	public static MyChat getAddon() {
		return instance;
	}

	public static FileManager getChatsFile() {
		return CHATS;
	}

	public static FileManager getFormatFile() {
		return FILE;
	}

	@Override
	public String[] getAuthors() {
		return new String[]{"Hempfest"};
	}

	@Override
	public String getName() {
		return "myChat";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getDescription() {
		return "Gives access to complete chat control";
	}

	public IChatMetaLoader getLoader() {
		return loader;
	}

	public Permissions getPermissions() {
		return LabyrinthProvider.getInstance().getServicesManager().load(Permissions.class);
	}

	public static @Nullable
	Cooldown getCooldown(Player target) {
		return Cooldown.getById("MyChatC-" + target.getUniqueId());
	}


	@Override
	public void onLoad() {
		instance = this;
		loader = new IChatMetaLoader();
		final MyListener listener = new MyListener();
		listener.runTimerUpdates();
		getContext().stage(listener);
		MySettings.loadDefaults();
		LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.SYNCHRONOUS).repeat(task -> {
			Permissions perm = getPermissions();
			if (perm != null && perm.isGroupsAllowed()) {
				Essentials.getInstance().getLogger().info("- Loaded groups.");
				for (String group : Arrays.stream(perm.getGroups()).map(Group::getName).toArray(String[]::new)) {
					MySettings.generateSlot(group);
					List<IChatComponentMeta> metaList = new ArrayList<>();
					for (String i : FILE.getRoot().getNode(group).getKeys(false)) {
						if (!i.equals("fallback")) {
							IChatComponentMeta meta = new IChatComponentMeta(group, Integer.parseInt(i));
							metaList.add(meta);
							getLoader().loadMeta(group, metaList);
						}
					}
				}
				task.cancel();
			}
		}, HUID.randomID().toString(), TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
		for (Map.Entry<Integer, List<String>> hPrint : MySettings.TABLIST_HEADER.grab().entrySet()) {
			getLoader().HEADER_QUEUE.put(hPrint.getKey(), hPrint.getValue());
		}
		for (Map.Entry<Integer, List<String>> fPrint : MySettings.TABLIST_FOOTER.grab().entrySet()) {
			getLoader().FOOTER_QUEUE.put(fPrint.getKey(), fPrint.getValue());
		}
		TablistUpdate.load();
		for (String channel : CHATS.getRoot().getKeys(false)) {
			String tag = CHATS.getRoot().getString(channel + ".tag");
			String node = CHATS.getRoot().getString(channel + ".node");
			boolean def = CHATS.getRoot().getBoolean(channel + ".is-main");
			assert tag != null;
			assert node != null;
			IChatChannel ch = new IChatChannel(tag, node, def);
			getLoader().loadChannel(ch);
		}

		ChatComponentUtil.loadColors();
		// ============== [ Command registration below ] =========== //


		CommandMapper.from(MyChatCommand.MAIL)
				.apply((builder, p, commandLabel, args) -> {
					if (args.length == 0) {
						builder.sendUsage(p);
					}
					if (args.length == 1) {
						PlayerSearch search = PlayerSearch.look(args[0]);
						if (search.isValid()) {
							FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
							if (!user.getRoot().getStringList(p.getUniqueId() + ".mail." + search.getId().toString()).isEmpty()) {
								List<String> mail = new ArrayList<>(user.getRoot().getStringList(p.getUniqueId() + ".mail." + search.getId().toString()));
								mail.forEach(s -> {
									Message.form(p).send("&7" + search.getOfflinePlayer().getName() + "&r: &3" + s);
									Schedule.sync(() -> {
										mail.remove(s);
										user.getRoot().set(p.getUniqueId() + ".mail." + search.getId().toString(), mail);
										user.getRoot().save();
									}).run();
								});
							} else {
								builder.sendMessage(p, "&cYou have no mail from this user.");
							}
						} else {
							builder.sendMessage(p, "&cThe player you specified was not found.");
						}
					}
				})
				.next((builder, sender, commandLabel, args) -> builder.sendMessage(sender, "This is a player only command."))
				.read((builder, sender, commandLabel, args) -> {
					return null;
				});

		CommandMapper.load(MyChatCommand.MESSAGE, () -> messageTab = SimpleTabCompletion.empty())
				.apply((builder, p, commandLabel, args) -> {
					if (builder.testPermission(p)) {
						if (args.length == 0) {
							// need player and msg
							builder.sendUsage(p);
							return;
						}
						if (args.length == 1) {
							// need message
							builder.sendUsage(p);
							return;
						}
						PlayerSearch search = PlayerSearch.look(args[0]);

						if (search.isValid()) {
							FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
							user.getRoot().set(search.getId().toString() + ".message", p.getUniqueId().toString());
							user.getRoot().set(p.getUniqueId() + ".message", search.getId().toString());
							user.getRoot().save();
							StringBuilder message = new StringBuilder();
							for (int i = 1; i < args.length; i++) {
								message.append(args[i]).append(" ");
							}
							if (search.isOnline()) {
								Player target = search.getPlayer();
								Message.form(p).build(TextLib.getInstance().textSuggestable(MySettings.MESSAGE_OUT.getString().replace("{TARGET}", search.getPlayer().getName()), MySettings.MESSAGE_OUT_HOVER.getString().replace("{MESSAGE}", message.toString().trim()), MySettings.MESSAGE_OUT_META.getString(), "reply " + search.getPlayer().getName() + " "));
								Message.form(target).build(TextLib.getInstance().textSuggestable(MySettings.MESSAGE_IN.getString().replace("{SENDER}", p.getName()), MySettings.MESSAGE_IN_HOVER.getString().replace("{MESSAGE}", message.toString().trim()), MySettings.MESSAGE_IN_META.getString(), "reply " + p.getName() + " "));
							} else {
								List<String> mailbox = user.getRoot().getStringList(search.getId().toString() + ".mail." + p.getUniqueId());
								if (mailbox.contains(message.toString().trim())) {
									builder.sendMessage(p, "&cPlease don't spam mail!");
									return;
								}
								mailbox.add(message.toString().trim());
								user.getRoot().set(search.getId().toString() + ".mail." + p.getUniqueId(), mailbox);
								user.getRoot().save();
								builder.sendMessage(p, "&cThey are offline right now but will receive your message when they log in next.");
								Message.form(p).build(TextLib.getInstance().textSuggestable(MySettings.MESSAGE_OUT.getString().replace("{TARGET}", search.getOfflinePlayer().getName()), MySettings.MESSAGE_OUT_HOVER.getString().replace("{MESSAGE}", message.toString().trim()), MySettings.MESSAGE_OUT_META.getString(), "reply " + search.getOfflinePlayer().getName() + " "));
							}
						} else {
							// not found
							builder.sendMessage(p, "&cThe target you specified was not found.");
						}
					}

				})
				.next((builder, sender, commandLabel, args) -> builder.sendMessage(sender, "This is a player only command."))
				.read((builder, sender, commandLabel, args) -> messageTab.fillArgs(args)
						.then(TabCompletionIndex.ONE, Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()))
						.get());

		CommandMapper.load(MyChatCommand.REPLY, () -> replyTab = SimpleTabCompletion.empty())
				.apply((builder, p, commandLabel, args) -> {
					if (builder.testPermission(p)) {
						if (args.length == 0) {
							// need player and msg
							builder.sendUsage(p);
							return;
						}

						FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
						String recipient = user.getRoot().getString(p.getUniqueId() + ".message");

						if (recipient == null) {
							builder.sendMessage(p, "&cYou have no one to reply to.");
							return;
						}

						PlayerSearch search = PlayerSearch.look(UUID.fromString(recipient));

						if (search.isValid()) {

							user.getRoot().set(search.getId().toString() + ".message", p.getUniqueId().toString());
							user.getRoot().set(p.getUniqueId() + ".message", search.getId().toString());
							user.getRoot().save();
							StringBuilder message = new StringBuilder();
							for (String arg : args) {
								message.append(arg).append(" ");
							}
							if (search.isOnline()) {
								Player target = search.getPlayer();
								Message.form(p).build(TextLib.getInstance().textSuggestable(MySettings.MESSAGE_OUT.getString().replace("{TARGET}", search.getPlayer().getName()), MySettings.MESSAGE_OUT_HOVER.getString().replace("{MESSAGE}", message.toString().trim()), MySettings.MESSAGE_OUT_META.getString(), "reply " + search.getPlayer().getName() + " "));
								Message.form(target).build(TextLib.getInstance().textSuggestable(MySettings.MESSAGE_IN.getString().replace("{SENDER}", p.getName()), MySettings.MESSAGE_IN_HOVER.getString().replace("{MESSAGE}", message.toString().trim()), MySettings.MESSAGE_IN_META.getString(), "reply " + p.getName() + " "));
							} else {
								List<String> mailbox = user.getRoot().getStringList(search.getId().toString() + ".mail." + p.getUniqueId());
								if (mailbox.contains(message.toString().trim())) {
									builder.sendMessage(p, "&cPlease don't spam mail!");
									return;
								}
								mailbox.add(message.toString().trim());
								user.getRoot().set(search.getId().toString() + ".mail." + p.getUniqueId(), mailbox);
								user.getRoot().save();
								builder.sendMessage(p, "&cThey are offline right now but will receive your message when they log in next.");
								Message.form(p).build(TextLib.getInstance().textSuggestable(MySettings.MESSAGE_OUT.getString().replace("{TARGET}", search.getOfflinePlayer().getName()), MySettings.MESSAGE_OUT_HOVER.getString().replace("{MESSAGE}", message.toString().trim()), MySettings.MESSAGE_OUT_META.getString(), "reply " + search.getOfflinePlayer().getName() + " "));
							}
						} else {
							// not found
							builder.sendMessage(p, "&cThe target you specified was not found.");
						}
					}

				})
				.next((builder, sender, commandLabel, args) -> builder.sendMessage(sender, "This is a player only command."))
				.read((builder, p, commandLabel, args) -> replyTab.fillArgs(args)
						.get());

		CommandMapper.from(MyChatCommand.MUTE)
				.apply((builder, p, commandLabel, args) -> {
					if (builder.testPermission(p)) {
						if (args.length == 0) {
							builder.sendUsage(p);
							return;
						}
						if (args.length == 1) {
							PlayerSearch search = PlayerSearch.look(args[0]);
							if (search.isValid()) {
								if (search.isOnline()) {
									Player target = search.getPlayer();
									if (!MyChatMute.muted(target)) {
										MyChatMute.mute(target);
										builder.sendMessage(p, "&a" + target.getName() + " has been muted.");
									} else {
										MyChatMute.unmute(target);
										builder.sendMessage(p, "&a" + target.getName() + " has been un-muted.");
									}
								}
							} else {
								builder.sendMessage(p, "&cTarget player not found.");
							}
							return;
						}
						if (args.length == 2) {
							long time;
							try {
								time = Long.parseLong(args[1]);
							} catch (NumberFormatException e) {
								builder.sendUsage(p);
								return;
							}
							PlayerSearch search = PlayerSearch.look(args[0]);
							if (search.isValid()) {
								if (search.isOnline()) {
									Player target = search.getPlayer();
									if (!MyChatMute.muted(target)) {
										MyChatMute.mute(target, time);
										builder.sendMessage(p, "&a" + target.getName() + " has been muted for " + MyChatMute.get(target).fullTimeLeft());
									} else {
										MyChatMute.unmute(target);
										builder.sendMessage(p, "&a" + target.getName() + " has been un-muted.");
									}
								}
							} else {
								builder.sendMessage(p, "&cTarget player not found.");
							}
						}
					}
				})
				.next((builder, sender, commandLabel, args) -> {

				})
				.read(CommandBuilder::defaultCompletion);

		CommandMapper.load(MyChatCommand.CHAT, () -> chatTab = SimpleTabCompletion.empty())
				.apply((builder, p, commandLabel, args) -> {
					if (builder.testPermission(p)) {
						if (args.length == 0) {
							new PaginatedList<>(Arrays.asList("/&6" + commandLabel + " delete", "/&6" + commandLabel + " reload", "/&6" + commandLabel + " spy", "/&6" + commandLabel + " color")).limit(5)
									.finish(b -> b.setSuffix("&r▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").setPrefix("&r▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").setPlayer(p)).start((pagination, page, max) -> {
										Message msg = Message.form(p);
										msg.send(MyEssentialsAPI.getInstance().getPrefix() + " &r- Chat management commands. &r(&7/chat #page&r)");
										msg.send("&r▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
									}).decorate((pagination, object, page, max, placement) -> Message.form(p).send(object)).get(1);
							return;
						}
						if (args.length == 1) {
							// reload, spy, channel-name
							if (args[0].equalsIgnoreCase("color")) {
								if (!p.hasPermission("mess.chat.color")) {
									builder.sendMessage(p, "&cYou have no permission.");
									return;
								}
								//ColorPicker.view(p).open(p);
							}
							if (args[0].equalsIgnoreCase("delete")) {
								// not enough args

								return;
							}
							if (args[0].equalsIgnoreCase("mute")) {
								if (!p.hasPermission("mess.chat.mute")) {
									builder.sendMessage(p, "&cYou have no permission.");
									return;
								}

								return;
							}
							if (args[0].equalsIgnoreCase("reload")) {
								if (!p.hasPermission("mess.chat.reload")) {
									builder.sendMessage(p, "&cYou have no permission.");
									return;
								}
								MySettings.getSettings().getRoot().reload();
								FILE.getRoot().reload();
								CHATS.getRoot().reload();
								OrdinalProcedure.process(getAddon().getLoader(), 3);
								OrdinalProcedure.process(getAddon().getLoader(), 4);
								OrdinalProcedure.process(getAddon().getLoader(), 7);
								MySettings.loadDefaults();
								ChatComponentUtil.loadColors();
								for (String group : Arrays.stream(getPermissions().getGroups()).map(Group::getName).toArray(String[]::new)) {
									MySettings.generateSlot(group);
									List<IChatComponentMeta> metaList = new ArrayList<>();
									for (String i : FILE.getRoot().getNode(group).getKeys(false)) {
										if (!i.equals("fallback")) {
											IChatComponentMeta meta = new IChatComponentMeta(group, Integer.parseInt(i));
											metaList.add(meta);
											getAddon().getLoader().loadMeta(group, metaList);
										}
									}
								}
								for (String channel : CHATS.getRoot().getKeys(false)) {
									String tag = CHATS.getRoot().getString(channel + ".tag");
									String node = CHATS.getRoot().getString(channel + ".node");
									boolean def = CHATS.getRoot().getBoolean(channel + ".is-main");
									assert tag != null;
									assert node != null;
									IChatChannel ch = new IChatChannel(tag, node, def);
									getAddon().getLoader().loadChannel(ch);
								}
								if (MySettings.TABLIST_USED.valid()) {
									for (Map.Entry<Integer, List<String>> hPrint : MySettings.TABLIST_HEADER.grab().entrySet()) {
										getLoader().HEADER_QUEUE.put(hPrint.getKey(), hPrint.getValue());
									}
									for (Map.Entry<Integer, List<String>> fPrint : MySettings.TABLIST_FOOTER.grab().entrySet()) {
										getLoader().FOOTER_QUEUE.put(fPrint.getKey(), fPrint.getValue());
									}
									TablistUpdate.load();
								}
								for (Player online : Bukkit.getOnlinePlayers()) {
									if (getAddon().getLoader().getChatChannels().stream().noneMatch(ch -> ch.getUsers().contains(online))) {
										for (IChatChannel channel : getAddon().getLoader().getChatChannels()) {
											if (channel.isDefault()) {
												channel.addUser(online);
												break;
											}
										}
									}
								}
								MyEssentialsAPI.getInstance().getMessenger().broadcastMessagePrefixed(p, " &2The chat configuration has been reloaded along with all detected cache systems. Every player has been defaulted to the main chat channel.");
								return;
							}
							if (args[0].equalsIgnoreCase("spy")) {
								if (!p.hasPermission("mess.chat.spy")) {
									builder.sendMessage(p, "&cYou have no permission.");
									return;
								}
								if (getAddon().getLoader().isSpying(p)) {
									getAddon().getLoader().removeSpy(p);
									builder.sendMessage(p, "&aNow leaving channel spy mode.");
								} else {
									getAddon().getLoader().newSpy(p);
									builder.sendMessage(p, "&aNow spying on all chat channels.");
								}
								return;
							}
							IChatChannel current = getAddon().getLoader().getChannel(p);

							if (current == null) {
								builder.sendMessage(p, "&cNo chat channel found, please re-log.");
								return;
							}

							IChatChannel channel = getAddon().getLoader().getChatChannels().stream().filter(ch -> ch.getChannel().equalsIgnoreCase(args[0])).findFirst().orElse(null);
							if (channel != null) {
								if (channel.getChannel().equals(current.getChannel())) {
									builder.sendMessage(p, "&cYou are already participating in this chat channel.");
									return;
								}
								builder.sendMessage(p, "&eNow leaving " + current.getChannel() + "...");
								current.removeUser(p);
								channel.addUser(p);
								channel.clear(p);
								builder.sendMessage(p, "&aNow searching for recent conversation history for " + channel.getChannel() + "...");
								if (getAddon().getLoader().getTrails().stream().noneMatch(v -> v.getChannel() == channel)) {
									builder.sendMessage(p, "&3No conversation history found. Channel empty.");
								} else {
									for (IChatComponentTrail trail : getAddon().getLoader().getTrails().stream().sorted(IChatComponentTrail::compareTo).collect(Collectors.toCollection(LinkedHashSet::new))) {
										if (trail != null && trail.getChannel() == channel) {
											if (trail.getChannel() == channel) {
												String group = getAddon().getPermissions().getUser(trail.getSender()).getGroup().getName();
												if (trail.getSender().isOnline()) {
													List<BaseComponent> toSend = ChatComponentUtil.getFormat(p, trail.getSender(), group, trail.getMessage(), trail.getKey());
													BaseComponent[] result = toSend.toArray(new BaseComponent[toSend.size() - 1]);
													p.spigot().sendMessage(result);
												}
											}
										}
									}
								}
								builder.sendMessage(p, "&aYou have now switched to the " + channel.getChannel() + " channel.");
							} else {
								builder.sendMessage(p, "&cChat channel not found!");
							}

						}
						if (args.length == 2) {
							// delete
							if (args[0].equalsIgnoreCase("color")) {
								PlayerSearch search = PlayerSearch.look(args[1]);
								if (!p.hasPermission("mess.chat.color.other")) {
									builder.sendMessage(p, "&cYou have no permission.");
									return;
								}
								if (search.getPlayer() != null) {
									//ColorPicker.view(search.getPlayer()).open(p);
								} else {

								}
							}
							if (args[0].equalsIgnoreCase("mute")) {
								if (!p.hasPermission("mess.chat.mute")) {
									builder.sendMessage(p, "&cYou have no permission.");
									return;
								}
								IChatChannel channel = getAddon().getLoader().getChatChannels().stream().filter(ch -> ch.getChannel().equalsIgnoreCase(args[1])).findFirst().orElse(null);
								if (channel != null) {
									if (channel.isMuted()) {
										builder.sendMessage(p, "&aYou have un-muted chat " + channel.getChannel());
										channel.setMuted(false);
									} else {
										builder.sendMessage(p, "&aYou have muted chat " + channel.getChannel());
										channel.setMuted(true);
									}
								}
								return;
							}
							if (args[0].equalsIgnoreCase("delete")) {
								if (!p.hasPermission("mess.chat.delete")) {
									builder.sendMessage(p, "&cYou have no permission.");
									return;
								}
								try {
									Integer.parseInt(args[1]);
								} catch (NumberFormatException e) {
									// invalid number
								}
								if (getAddon().getLoader().getTrail(Integer.parseInt(args[1])) == null) {
									builder.sendMessage(p, "&cMessage no longer exists.");
									return;
								}

								IChatComponentTrail key = getAddon().getLoader().getTrail(Integer.parseInt(args[1]));
								Schedule.sync(() -> {
									String group = getAddon().getPermissions().getUser(key.getSender()).getGroup().getName();
									for (Player recipient : key.getChannel().getUsers()) {
										key.getChannel().clear(recipient);
									}
									for (IChatComponentTrail trail : getAddon().getLoader().getTrails().stream().sorted(IChatComponentTrail::compareTo).collect(Collectors.toCollection(LinkedHashSet::new))) {
										if (!trail.equals(key)) {
											OrdinalProcedure.select(getAddon().getLoader(), 0, getAddon().getLoader().getTrails().size(), trail);
											for (Player recipient : key.getChannel().getUsers()) {
												List<BaseComponent> toSend = ChatComponentUtil.getFormat(recipient, trail.getSender(), group, trail.getMessage(), getAddon().getLoader().getTrails().size());
												BaseComponent[] result = toSend.toArray(new BaseComponent[0]);
												recipient.spigot().sendMessage(result);
											}
										}
									}
								}).applyAfter(() -> {
									OrdinalProcedure.select(getAddon().getLoader(), 2, Integer.parseInt(args[1]));
									builder.sendMessage(p, "&aChat message removed.");
								}).wait(1);
							}
							if (args[0].equalsIgnoreCase("approve")) {
								try {
									Integer.parseInt(args[1]);
								} catch (NumberFormatException e) {
									// invalid number
								}
								if (getAddon().getLoader().getWitheld(Integer.parseInt(args[1])) == null) {
									builder.sendMessage(p, "&cMessage no longer exists.");
									return;
								}

								IChatComponentTrail key = getAddon().getLoader().getWitheld(Integer.parseInt(args[1]));
								String group = getAddon().getPermissions().getUser(key.getSender()).getGroup().getName();
								for (Player recipient : key.getChannel().getUsers()) {
									List<BaseComponent> toSend = ChatComponentUtil.getFormat(recipient, key.getSender(), group, key.getMessage(), key.getKey());
									BaseComponent[] result = toSend.toArray(new BaseComponent[toSend.size() - 1]);
									recipient.spigot().sendMessage(result);
								}
								OrdinalProcedure.select(getAddon().getLoader(), 0, key.getKey(), key);
								Schedule.async(() -> OrdinalProcedure.select(getAddon().getLoader(), 6, Integer.parseInt(args[1]))).wait(9);
								builder.sendMessage(p, "&aChat message approved.");
							}
							if (args[0].equalsIgnoreCase("deny")) {
								try {
									Integer.parseInt(args[1]);
								} catch (NumberFormatException e) {
									// invalid number
								}
								if (getAddon().getLoader().getWitheld(Integer.parseInt(args[1])) == null) {
									builder.sendMessage(p, "&cMessage no longer exists.");
									return;
								}
								IChatComponentTrail key = getAddon().getLoader().getWitheld(Integer.parseInt(args[1]));
								Schedule.async(() -> OrdinalProcedure.select(getAddon().getLoader(), 6, Integer.parseInt(args[1]))).wait(9);
								builder.sendMessage(key.getSender(), "&c&oA staff member has denied your message.");
								builder.sendMessage(p, "&aChat message denied.");
							}
							if (args[0].equalsIgnoreCase("message")) {
								builder.sendUsage(p);
							}
						}

					}
				})
				.next((builder, sender, commandLabel, args) -> {
					if (args.length == 0) {
						builder.sendMessage(sender, "More arguments expected.");
					}
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("reload")) {
							MySettings.getSettings().getRoot().reload();
							FILE.getRoot().reload();
							CHATS.getRoot().reload();
							OrdinalProcedure.process(getAddon().getLoader(), 3);
							OrdinalProcedure.process(getAddon().getLoader(), 4);
							OrdinalProcedure.process(getAddon().getLoader(), 7);
							MySettings.loadDefaults();
							ChatComponentUtil.loadColors();
							for (String group : Arrays.stream(getPermissions().getGroups()).map(Group::getName).toArray(String[]::new)) {
								MySettings.generateSlot(group);
								List<IChatComponentMeta> metaList = new ArrayList<>();
								for (String i : FILE.getRoot().getNode(group).getKeys(false)) {
									if (!i.equals("fallback")) {
										IChatComponentMeta meta = new IChatComponentMeta(group, Integer.parseInt(i));
										metaList.add(meta);
										getAddon().getLoader().loadMeta(group, metaList);
									}
								}
							}
							for (String channel : CHATS.getRoot().getKeys(false)) {
								String tag = CHATS.getRoot().getString(channel + ".tag");
								String node = CHATS.getRoot().getString(channel + ".node");
								boolean def = CHATS.getRoot().getBoolean(channel + ".is-main");
								assert tag != null;
								assert node != null;
								IChatChannel ch = new IChatChannel(tag, node, def);
								getAddon().getLoader().loadChannel(ch);
							}
							if (MySettings.TABLIST_USED.valid()) {
								for (Map.Entry<Integer, List<String>> hPrint : MySettings.TABLIST_HEADER.grab().entrySet()) {
									getLoader().HEADER_QUEUE.put(hPrint.getKey(), hPrint.getValue());
								}
								for (Map.Entry<Integer, List<String>> fPrint : MySettings.TABLIST_FOOTER.grab().entrySet()) {
									getLoader().FOOTER_QUEUE.put(fPrint.getKey(), fPrint.getValue());
								}
								TablistUpdate.load();
							}
							for (Player online : Bukkit.getOnlinePlayers()) {
								if (getAddon().getLoader().getChatChannels().stream().noneMatch(ch -> ch.getUsers().contains(online))) {
									for (IChatChannel channel : getAddon().getLoader().getChatChannels()) {
										if (channel.isDefault()) {
											channel.addUser(online);
											break;
										}
									}
								}
							}
							MyEssentialsAPI.getInstance().getMessenger().broadcastMessagePrefixed(sender, "&2The chat configuration has been reloaded along with all detected cache systems. Every player has been defaulted to the main chat channel.");
							return;
						}
						builder.sendMessage(sender, "Channel switching is for players only!");
					}
				})
				.read((builder, p, commandLabel, args) -> chatTab.fillArgs(args)
						.then(TabCompletionIndex.ONE, getAddon().getLoader().getChatChannels().stream().filter(c -> p.hasPermission(c.getPermission())).map(IChatChannel::getChannel).collect(Collectors.toList()))
						.get());


	}

	@Override
	protected void onEnable() {

	}

	@Override
	protected void onDisable() {

	}

	@Override
	public boolean isStaged() {
		return true;
	}

}
