package com.github.sanctum.mychat;

import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.formatting.TabCompletion;
import com.github.sanctum.labyrinth.formatting.TabCompletionBuilder;
import com.github.sanctum.labyrinth.formatting.string.PaginatedAssortment;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.mychat.gui.ColorPicker;
import com.github.sanctum.mychat.model.AsyncChatChannel;
import com.github.sanctum.mychat.model.IChatComponentMeta;
import com.github.sanctum.mychat.model.IChatComponentTrail;
import com.github.sanctum.mychat.model.IChatMetaLoader;
import com.github.sanctum.mychat.util.ChatComponentUtil;
import com.github.sanctum.mychat.util.TablistUpdate;
import com.github.sanctum.myessentials.Essentials;
import com.github.sanctum.myessentials.api.EssentialsAddon;
import com.github.sanctum.myessentials.api.MyEssentialsAPI;
import com.github.sanctum.myessentials.model.CommandBuilder;
import com.github.sanctum.myessentials.model.CommandData;
import com.github.sanctum.myessentials.model.CommandMapper;
import com.github.sanctum.myessentials.util.moderation.PlayerSearch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.BaseComponent;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

public final class MyChat extends EssentialsAddon {

	protected final Map<CommandData, Class<? extends CommandBuilder>> COMMANDS = new HashMap<>();
	protected final Map<Object, Object> DATA = new HashMap<>();
	protected final Collection<Listener> LISTENERS = new HashSet<>();
	protected final IChatMetaLoader loader = new IChatMetaLoader();
	protected TabCompletionBuilder chatTab;
	protected static FileManager FILE = MyEssentialsAPI.getInstance().getAddonFile("Format", "Chat");
	protected static FileManager CHATS = MyEssentialsAPI.getInstance().getAddonFile("Channels", "Chat");
	protected static Permission permissionBase = Bukkit.getServicesManager().load(Permission.class);
	protected static MyChat instance;

	{
		instance = this;
	}


	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean isStandalone() {
		return true;
	}

	@Override
	public EssentialsAddon getInstance() {
		return this;
	}

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
	public String getAddonName() {
		return "myChat";
	}

	@Override
	public String getAddonDescription() {
		return "Gives access to complete chat control";
	}

	@Override
	public Collection<Listener> getListeners() {
		return LISTENERS;
	}

	@Override
	public Map<CommandData, Class<? extends CommandBuilder>> getCommands() {
		return COMMANDS;
	}

	@Override
	public Map<Object, Object> getData() {
		return DATA;
	}

	public static Permission getPermissionBase() {
		return permissionBase;
	}

	public IChatMetaLoader getLoader() {
		return loader;
	}

	public static @Nullable
	Cooldown getCooldown(Player target) {
		return Cooldown.getById("MyChatC-" + target.getUniqueId().toString());
	}


	@Override
	protected void apply() {
		final MyListener listener = new MyListener();
		listener.runTimerUpdates();
		LISTENERS.add(listener);
		MySettings.loadDefaults();
		if (permissionBase == null) {
			Bukkit.getPluginManager().disablePlugin(Essentials.getInstance());
			return;
		}
		for (String group : permissionBase.getGroups()) {
			MySettings.generateSlot(group);
			List<IChatComponentMeta> metaList = new ArrayList<>();
			for (String i : FILE.getConfig().getConfigurationSection(group).getKeys(false)) {
				if (!i.equals("fallback")) {
					IChatComponentMeta meta = new IChatComponentMeta(group, Integer.parseInt(i));
					metaList.add(meta);
					getLoader().META_MAP.put(group, metaList);
				}
			}
		}
		for (Map.Entry<Integer, List<String>> hPrint : MySettings.TABLIST_HEADER.grab().entrySet()) {
			getLoader().HEADER.put(hPrint.getKey(), hPrint.getValue());
		}
		for (Map.Entry<Integer, List<String>> fPrint : MySettings.TABLIST_FOOTER.grab().entrySet()) {
			getLoader().FOOTER.put(fPrint.getKey(), fPrint.getValue());
		}
		TablistUpdate.load();
		for (String channel : CHATS.getConfig().getKeys(false)) {
			String tag = CHATS.getConfig().getString(channel + ".tag");
			String node = CHATS.getConfig().getString(channel + ".node");
			boolean def = CHATS.getConfig().getBoolean(channel + ".is-main");
			assert tag != null;
			assert node != null;
			AsyncChatChannel ch = new AsyncChatChannel(tag, node, def);
			getLoader().CHAT_CHANNELS.add(ch);
		}


		// ============== [ Command registration below ] =========== //


		CommandMapper.load(MyChatCommand.CHAT, () -> chatTab = TabCompletion.build(MyChatCommand.CHAT.getLabel()))
				.apply((builder, p, commandLabel, args) -> {
					if (builder.testPermission(p)) {
						if (args.length == 0) {
							PaginatedAssortment menu = new PaginatedAssortment(p, Arrays.asList("/" + commandLabel + " delete", "/" + commandLabel + " reload", "/" + commandLabel + " spy", "/" + commandLabel + " color"))
									.setLinesPerPage(5)
									.setListTitle("&7Chat management commands. &r(&7/chat #page&r)")
									.setListBorder("&r▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
									.setNavigateCommand(commandLabel);
							menu.export(1);
							return;
						}
						if (args.length == 1) {
							// reload, spy, channel-name
							if (args[0].equalsIgnoreCase("delete")) {
								// not enough args

								return;
							}
							if (args[0].equalsIgnoreCase("reload")) {
								MySettings.getSettings().reload();
								FILE.reload();
								CHATS.reload();
								getAddon().getLoader().RESEND_MAP.clear();
								getAddon().getLoader().META_MAP.clear();
								getAddon().getLoader().CHAT_CHANNELS.clear();
								getAddon().getLoader().RELOADED = true;
								MySettings.loadDefaults();
								for (String group : permissionBase.getGroups()) {
									MySettings.generateSlot(group);
									List<IChatComponentMeta> metaList = new ArrayList<>();
									for (String i : FILE.getConfig().getConfigurationSection(group).getKeys(false)) {
										if (!i.equals("fallback")) {
											IChatComponentMeta meta = new IChatComponentMeta(group, Integer.parseInt(i));
											metaList.add(meta);
											getAddon().getLoader().META_MAP.put(group, metaList);
										}
									}
								}
								for (String channel : CHATS.getConfig().getKeys(false)) {
									String tag = CHATS.getConfig().getString(channel + ".tag");
									String node = CHATS.getConfig().getString(channel + ".node");
									boolean def = CHATS.getConfig().getBoolean(channel + ".is-main");
									assert tag != null;
									assert node != null;
									AsyncChatChannel ch = new AsyncChatChannel(tag, node, def);
									getAddon().getLoader().CHAT_CHANNELS.add(ch);
								}
								if (MySettings.TABLIST_USED.valid()) {
									for (Map.Entry<Integer, List<String>> hPrint : MySettings.TABLIST_HEADER.grab().entrySet()) {
										getLoader().HEADER.put(hPrint.getKey(), hPrint.getValue());
									}
									for (Map.Entry<Integer, List<String>> fPrint : MySettings.TABLIST_FOOTER.grab().entrySet()) {
										getLoader().FOOTER.put(fPrint.getKey(), fPrint.getValue());
									}
									TablistUpdate.load();
								}
								Schedule.sync(() -> getAddon().getLoader().RELOADED = false).waitReal(20 * 3);
								for (Player online : Bukkit.getOnlinePlayers()) {
									Schedule.sync(() -> {
										if (MySettings.TABLIST_USED.valid()) {
											TablistUpdate.to(online);
										}
									}).waitReal(20 * 3);
									if (getAddon().getLoader().getChatChannels().stream().noneMatch(ch -> ch.getUsers().contains(online))) {
										for (AsyncChatChannel channel : getAddon().getLoader().getChatChannels()) {
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
								if (getAddon().getLoader().SPY_LOG.contains(p)) {
									getAddon().getLoader().SPY_LOG.remove(p);
									builder.sendMessage(p, "&aNow leaving channel spy mode.");
								} else {
									getAddon().getLoader().SPY_LOG.add(p);
									builder.sendMessage(p, "&aNow spying on all chat channels.");
								}
								return;
							}

							AsyncChatChannel current = getAddon().getLoader().getChannel(p);

							if (current == null) {
								// No channel found. Please relog.
								return;
							}

							AsyncChatChannel channel = getAddon().getLoader().getChatChannels().stream().filter(ch -> ch.getChannel().equalsIgnoreCase(args[0])).findFirst().orElse(null);
							if (channel != null) {
								if (channel.getChannel().equals(current.getChannel())) {
									builder.sendMessage(p, "&cYou are already participating in this chat channel.");
									return;
								}
								builder.sendMessage(p, "&eNow leaving " + current.getChannel() + "...");
								current.removeUser(p);
								channel.addUser(p);
								channel.clear(p);
								LinkedList<IChatComponentTrail> componentTrails = new LinkedList<>();
								int i = 0;
								builder.sendMessage(p, "&aNow searching for recent conversation history for " + channel.getChannel() + "...");
								for (Map.Entry<Integer, IChatComponentTrail> entry : getAddon().getLoader().RESEND_MAP.entrySet()) {
									if (entry.getValue().getChannel() == channel) {
										componentTrails.add(entry.getValue());
										i++;
									}
								}
								if (i == 0) {
									builder.sendMessage(p, "&3No conversation history found. Channel empty.");
									builder.sendMessage(p, "&aYou have now switched to the " + channel.getChannel() + " channel.");
								} else {
									componentTrails.sort(Comparator.comparingInt(IChatComponentTrail::getKey));
									for (IChatComponentTrail trail : componentTrails) {
										String group = getPermissionBase().getPrimaryGroup(trail.getSender());
										Schedule.async(() -> {
											if (trail.getSender().isOnline()) {
												List<BaseComponent> toSend = ChatComponentUtil.getFormat(p, trail.getSender(), group, trail.getMessage(), trail.getKey());
												BaseComponent[] result = toSend.toArray(new BaseComponent[toSend.size() - 1]);
												p.spigot().sendMessage(result);
											}
										}).wait(6);
									}
									Schedule.async(() -> builder.sendMessage(p, "&aYou have now switched to the " + channel.getChannel() + " channel.")).wait(8);
								}
							} else {
								builder.sendMessage(p, "&cChat channel not found!");
							}

						}
						if (args.length == 2) {
							// delete
							if (args[0].equalsIgnoreCase("color")) {
								PlayerSearch search = PlayerSearch.look(args[1]);
								if (search.isValid()) {
									ColorPicker.view(search.getPlayer()).open(p);
								} else {

								}
							}
							if (args[0].equalsIgnoreCase("delete")) {
								try {
									Integer.parseInt(args[1]);
								} catch (NumberFormatException e) {
									// invalid number
								}
								if (getAddon().getLoader().getMessage(Integer.parseInt(args[1])) == null) {
									builder.sendMessage(p, "&cMessage no longer exists.");
									return;
								}

								IChatComponentTrail key = getAddon().getLoader().RESEND_MAP.get(Integer.parseInt(args[1]));
								LinkedList<IChatComponentTrail> TRAILS = new LinkedList<>();
								for (Map.Entry<Integer, IChatComponentTrail> trailEntry : getAddon().getLoader().RESEND_MAP.entrySet()) {
									if (trailEntry.getValue().getChannel() == key.getChannel()) {
										if (trailEntry.getValue() != key) {
											TRAILS.add(trailEntry.getValue());
										} else {
											for (Player recipient : trailEntry.getValue().getChannel().getUsers()) {
												Schedule.async(() -> trailEntry.getValue().getChannel().clear(recipient)).run();
											}
										}
									}
								}
								TRAILS.sort(Comparator.comparingInt(IChatComponentTrail::getKey));
								for (IChatComponentTrail trail : TRAILS) {
									String group = getPermissionBase().getPrimaryGroup(trail.getSender());
									for (Player recipient : trail.getChannel().getUsers()) {
										Schedule.async(() -> trail.getChannel().clear(recipient)).applyAfter(() -> Schedule.async(() -> {
											List<BaseComponent> toSend = ChatComponentUtil.getFormat(recipient, trail.getSender(), group, trail.getMessage(), trail.getKey());
											BaseComponent[] result = toSend.toArray(new BaseComponent[toSend.size() - 1]);
											recipient.spigot().sendMessage(result);
										}).wait(6)).run();
									}
								}
								Schedule.async(() -> getAddon().getLoader().RESEND_MAP.remove(Integer.parseInt(args[1]))).wait(9);
								Schedule.async(() -> builder.sendMessage(p, "&aChat message removed.")).wait(8);
							}
						}
					}
				})
				.next((builder, sender, commandLabel, args) -> {
					if (args.length == 0) {

					}
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("reload")) {
							MySettings.getSettings().reload();
							FILE.reload();
							CHATS.reload();
							getAddon().getLoader().RESEND_MAP.clear();
							getAddon().getLoader().META_MAP.clear();
							getAddon().getLoader().CHAT_CHANNELS.clear();
							getAddon().getLoader().RELOADED = true;
							MySettings.loadDefaults();
							for (String group : permissionBase.getGroups()) {
								MySettings.generateSlot(group);
								List<IChatComponentMeta> metaList = new ArrayList<>();
								for (String i : FILE.getConfig().getConfigurationSection(group).getKeys(false)) {
									if (!i.equals("fallback")) {
										IChatComponentMeta meta = new IChatComponentMeta(group, Integer.parseInt(i));
										metaList.add(meta);
										getAddon().getLoader().META_MAP.put(group, metaList);
									}
								}
							}
							for (String channel : CHATS.getConfig().getKeys(false)) {
								String tag = CHATS.getConfig().getString(channel + ".tag");
								String node = CHATS.getConfig().getString(channel + ".node");
								boolean def = CHATS.getConfig().getBoolean(channel + ".is-main");
								assert tag != null;
								assert node != null;
								AsyncChatChannel ch = new AsyncChatChannel(tag, node, def);
								getAddon().getLoader().CHAT_CHANNELS.add(ch);
							}
							if (MySettings.TABLIST_USED.valid()) {
								for (Map.Entry<Integer, List<String>> hPrint : MySettings.TABLIST_HEADER.grab().entrySet()) {
									getLoader().HEADER.put(hPrint.getKey(), hPrint.getValue());
								}
								for (Map.Entry<Integer, List<String>> fPrint : MySettings.TABLIST_FOOTER.grab().entrySet()) {
									getLoader().FOOTER.put(fPrint.getKey(), fPrint.getValue());
								}
								TablistUpdate.load();
							}
							Schedule.sync(() -> getAddon().getLoader().RELOADED = false).waitReal(20 * 3);
							for (Player online : Bukkit.getOnlinePlayers()) {
								Schedule.sync(() -> {
									if (MySettings.TABLIST_USED.valid()) {
										TablistUpdate.to(online);
									}
								}).waitReal(20 * 3);
								if (getAddon().getLoader().getChatChannels().stream().noneMatch(ch -> ch.getUsers().contains(online))) {
									for (AsyncChatChannel channel : getAddon().getLoader().getChatChannels()) {
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
						// channel switching is for players only.
					}
				})
				.read((builder, p, commandLabel, args) -> {
					return chatTab.forArgs(args)
							.level(1)
							.completeAt(builder.getData().getLabel())
							.filter(() -> {
								List<String> list = getAddon().getLoader().getChatChannels().stream().filter(c -> p.hasPermission(c.getPermission())).map(AsyncChatChannel::getChannel).collect(Collectors.toList());
								if (builder.testPermission(p)) {
									list.add("");
								}
								return list;
							})
							.collect()
							.get(1);
				});


	}

}
