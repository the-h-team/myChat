package com.github.sanctum.mychat.gui;

public class ColorPicker {
/*
	private static final NamespacedKey key = new NamespacedKey(Essentials.getInstance(), "player_uuid");

	private static ItemStack markedItem(Material type, UUID id) {
		ItemStack item = new ItemStack(type);
		ItemMeta meta = item.getItemMeta();
		meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id.toString());
		item.setItemMeta(meta);
		return item;
	}

	private static boolean equalsIt(ItemStack item) {
		if (item.hasItemMeta()) {
			if (!item.getItemMeta().getPersistentDataContainer().isEmpty()) {
				return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) != null;
			}
		}
		return false;
	}

	public static AnvilMenu write(Player target) {
		return AnvilBuilder.from(StringUtils.use("&3Specify a color.").translate())
				.setLeftItem(builder -> {
					ItemStack paper = Items.edit().setType(Material.PAPER).setTitle("&aClick the other paper.").build();
					builder.setItem(paper);
					builder.setClick((player, text, args) -> {
						if (text.isEmpty()) {
							Message.form(player).send("&cNo color code is being provided.");
						} else {
							FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
							user.getConfig().set(target.getUniqueId().toString() + ".color", text);
							user.saveConfig();
							player.sendMessage(StringUtils.use(MyEssentialsAPI.getInstance().getPrefix() + " &a" + target.getName() + "'s" + " &2&ochat color was changed to &r" + '"').translate() + ChatComponentUtil.translate(player, text + "Text") + StringUtils.use("&r" + '"').translate());
						}
						player.closeInventory();
					});
				}).get();
	}

	public static Menu view(Player target) {
		return new MenuBuilder(InventoryRows.THREE, target.getDisplayName() + " Color Picker.")
				.addElement(markedItem(Material.BLACK_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Black:</#0caba8> &r'&0*0&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString() + ".color", "&0");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat color was changed to &r" + '"' + "&0Text&r" + '"');
				})
				.assignToSlots(0)
				.addElement(markedItem(Material.WHITE_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>White:</#0caba8> &r'&r*r&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString() + ".color", "&r");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat color was changed to &r" + '"' + "&rText&r" + '"');
				})
				.assignToSlots(1)
				.addElement(markedItem(Material.BLUE_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Blue:</#0caba8> &r'&9*9&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString() + ".color", "&9");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat color was changed to &r" + '"' + "&9Text&r" + '"');
				})
				.assignToSlots(2)
				.addElement(markedItem(Material.CYAN_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Cyan:</#0caba8> &r'#17ebe7*17ebe7&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString() + ".color", "#17ebe7");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat color was changed to &r" + '"' + "#17ebe7Text&r" + '"');
				})
				.assignToSlots(3)
				.addElement(markedItem(Material.GREEN_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Green:</#0caba8> &r'#17eb81*17eb81&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString() + ".color", "#17eb81");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat color was changed to &r" + '"' + "#17eb81Text&r" + '"');
				})
				.assignToSlots(4)
				.addElement(markedItem(Material.GRAY_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Gray:</#0caba8> &r'&8*8&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString() + ".color", "&8");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat color was changed to &r" + '"' + "&8Text&r" + '"');
				})
				.assignToSlots(5)
				.addElement(markedItem(Material.GREEN_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Green:</#0caba8> &r'&a*a&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString() + ".color", "&a");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat color was changed to &r" + '"' + "&aText&r" + '"');
				})
				.assignToSlots(5)
				.addElement(markedItem(Material.LIGHT_BLUE_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Light Blue:</#0caba8> &r'&b*b&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString() + ".color", "&b");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat color was changed to &r" + '"' + "&bText&r" + '"');
				})
				.assignToSlots(6)
				.addElement(markedItem(Material.LIGHT_GRAY_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Light Gray:</#0caba8> &r'&7*7&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString() + ".color", "&7");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat color was changed to &r" + '"' + "&7Text&r" + '"');
				})
				.assignToSlots(7)
				.addElement(markedItem(Material.NAME_TAG, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Custom</#0caba8>").translate())
				.setAction(click -> write(target).setViewer(click.getPlayer()).open())
				.assignToSlots(21)
				.addElement(markedItem(Material.NAME_TAG, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Reset</#0caba8>").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString() + ".color", null);
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat color was reset");
				})
				.assignToSlots(23)
				.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
				.setText(" ")
				.set()
				.cancelLowerInventoryClicks(false)
				.create(Essentials.getInstance());
	}

 */

}
