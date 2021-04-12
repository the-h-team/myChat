package com.github.sanctum.mychat.gui;

import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.gui.InventoryRows;
import com.github.sanctum.labyrinth.gui.menuman.Menu;
import com.github.sanctum.labyrinth.gui.menuman.MenuBuilder;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.myessentials.Essentials;
import com.github.sanctum.myessentials.api.MyEssentialsAPI;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ColorPicker {

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

	public static Menu view(Player target) {
		return new MenuBuilder(InventoryRows.THREE, target.getDisplayName() + " Color Picker.")
				.addElement(markedItem(Material.BLACK_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Black:</#0caba8> &r'&0*0&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString(), "&0");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat colored was changed to &r" + '"' + "&0Text&r" + '"');
				})
				.assignToSlots(0)
				.addElement(markedItem(Material.WHITE_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>White:</#0caba8> &r'&r*r&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString(), "&r");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat colored was changed to &r" + '"' + "&rText&r" + '"');
				})
				.assignToSlots(1)
				.addElement(markedItem(Material.BLUE_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Blue:</#0caba8> &r'&9*9&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString(), "&9");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat colored was changed to &r" + '"' + "&9Text&r" + '"');
				})
				.assignToSlots(2)
				.addElement(markedItem(Material.CYAN_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Cyan:</#0caba8> &r'#17ebe7*17ebe7&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString(), "#17ebe7");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat colored was changed to &r" + '"' + "#17ebe7Text&r" + '"');
				})
				.assignToSlots(3)
				.addElement(markedItem(Material.GREEN_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Green:</#0caba8> &r'#17eb81*17eb81&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString(), "#17eb81");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat colored was changed to &r" + '"' + "#17eb81Text&r" + '"');
				})
				.assignToSlots(4)
				.addElement(markedItem(Material.GRAY_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Gray:</#0caba8> &r'&8*8&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString(), "&8");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat colored was changed to &r" + '"' + "&8Text&r" + '"');
				})
				.assignToSlots(5)
				.addElement(markedItem(Material.GREEN_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Green:</#0caba8> &r'&a*a&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString(), "&a");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat colored was changed to &r" + '"' + "&aText&r" + '"');
				})
				.assignToSlots(5)
				.addElement(markedItem(Material.LIGHT_BLUE_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Light Blue:</#0caba8> &r'&b*b&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString(), "&b");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat colored was changed to &r" + '"' + "&bText&r" + '"');
				})
				.assignToSlots(6)
				.addElement(markedItem(Material.LIGHT_GRAY_WOOL, target.getUniqueId()))
				.setText(StringUtils.use("<#03fca5>Light Gray:</#0caba8> &r'&7*7&r'").translate())
				.setAction(click -> {
					FileManager user = MyEssentialsAPI.getInstance().getAddonFile("Users", "Chat/Data");
					user.getConfig().set(target.getUniqueId().toString(), "&7");
					user.saveConfig();
					Message.form(click.getPlayer()).setPrefix(MyEssentialsAPI.getInstance().getPrefix()).send("&a" + target.getName() + "'s" + " &2&ochat colored was changed to &r" + '"' + "&7Text&r" + '"');
				})
				.assignToSlots(7)
				.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
				.setText(" ")
				.set()
				.cancelLowerInventoryClicks(false)
				.create(Essentials.getInstance());
	}

}
