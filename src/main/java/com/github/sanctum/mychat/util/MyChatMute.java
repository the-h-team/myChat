package com.github.sanctum.mychat.util;

import com.github.sanctum.labyrinth.library.Cooldown;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

public class MyChatMute extends Cooldown {

	private static final List<Player> MUTED = new ArrayList<>();

	private final UUID user;
	private final long time;

	public MyChatMute(UUID user, long time) {
		this.user = user;
		this.time = abv((int) time);
	}

	public static Cooldown get(Player target) {
		return Cooldown.getById("My-Mute-" + target.getUniqueId().toString());
	}

	public static boolean muted(Player target) {
		return MUTED.contains(target);
	}

	public static void mute(Player target) {
		if (!muted(target)) {
			MUTED.add(target);
		}
	}

	public static void unmute(Player target) {
		if (muted(target)) {
			MUTED.remove(target);
			Cooldown mute = get(target);
			if (mute != null) {
				Cooldown.remove(mute);
			}
		}
	}

	public static void mute(Player target, long seconds) {
		if (!muted(target)) {
			MUTED.add(target);
			MyChatMute mute = new MyChatMute(target.getUniqueId(), seconds);
			mute.save();
		}
	}

	@Override
	public String getId() {
		return "My-Mute-" + user.toString();
	}

	@Override
	public long getCooldown() {
		return time;
	}
}
