package com.github.sanctum.mychat.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This class serves no real purpose as of right now. It is a reflective way to pass information via nms packet it works but hex color codes dont display properly.
 *
 * Hex... Why.... Oh well :p
 *
 */
public class CraftedPlayer {

	private final Player target;

	public CraftedPlayer(Player target) {
		this.target = target;
	}

	public void sendPacket(Object packet)
	{
		try
		{
			Object handle = target.getClass().getMethod("getHandle", new Class[0]).invoke(target);
			Field access = handle.getClass().getField("playerConnection");
			access.setAccessible(true);
			Object playerConnection = access.get(handle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, packet);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Class<?> getNMSClass(String name)
	{
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try
		{
			return Class.forName("net.minecraft.server." + version + "." + name);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Object chatComponent(String text) {
		try {
			Constructor<?> packet = getNMSClass("ChatComponentText").getDeclaredConstructor(String.class);
			return packet.newInstance(text);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object playerListPacket() {
		try {
			Constructor<?> packet = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getDeclaredConstructor();
			return packet.newInstance();
		} catch (Exception e) {

			return null;
		}
	}

}
