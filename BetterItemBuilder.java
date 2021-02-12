package com.booksaw.betterGuis.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.booksaw.betterGuis.message.MessageManager;

public class BetterItemBuilder extends com.booksaw.guiAPI.API.builder.ItemBuilder {

	public BetterItemBuilder(ItemStack item) {
		super(item);
	}

	public BetterItemBuilder(Material mat) {
		super(mat);
	}

	@Override
	protected String getMessage(String message) {
		return MessageManager.getMessage(message);
	}

}
