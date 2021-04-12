package com.github.sanctum.mychat.util;

import com.github.sanctum.labyrinth.library.Cooldown;
import java.util.UUID;

public class MyChatCooldown extends Cooldown {

	private final long time;
	private final UUID id;

	public MyChatCooldown(UUID user, int time) {
		this.time = abv(time);
		this.id = user;
	}

	@Override
	public String getId() {
		return "MyChatC-" + id.toString();
	}

	@Override
	public long getCooldown() {
		return time;
	}
}
