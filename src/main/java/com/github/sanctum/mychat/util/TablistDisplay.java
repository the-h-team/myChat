package com.github.sanctum.mychat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TablistDisplay {

	private final Map<Integer, String> messages = new HashMap<>();

	private TablistDisplay() {
	}

	public TablistDisplay input(int line, String text) {
		messages.put(line, text);
		return this;
	}

	@Override
	public String toString() {
		List<Integer> list = new ArrayList<>(messages.keySet());
		Collections.sort(list);
		StringBuilder builder = new StringBuilder();
		for (Integer i : list) {
			builder.append(messages.get(i)).append("\n&r");
		}
		int stop = builder.length() - 3;
		return builder.substring(0, stop);
	}

	public static TablistDisplay of() {
		return new TablistDisplay();
	}
}
