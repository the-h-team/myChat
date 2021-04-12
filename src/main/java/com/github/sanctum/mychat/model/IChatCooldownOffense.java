package com.github.sanctum.mychat.model;

import org.bukkit.entity.Player;

public class IChatCooldownOffense {

	private int level;

	private final Player offender;

	public IChatCooldownOffense(Player offender) {
		this.offender = offender;
	}

	public void invokeLevel() {
		this.level++;
	}

	public int getLevel() {
		return level;
	}

	public Player getOffender() {
		return offender;
	}
}
