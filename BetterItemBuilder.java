package com.booksaw.betterGuis.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.booksaw.betterGuis.message.MessageManager;

public class BetterItemBuilder extends com.booksaw.guiAPI.API.builder.ItemBuilder {

	public static BetterItemBuilder buildFromMessages(String path, Material mat) {
		BetterItemBuilder builder = (BetterItemBuilder) new BetterItemBuilder(mat).setName(path + ".name");

		for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {

			if (MessageManager.getMessage(path + ".lore." + alphabet, false) == null
					|| MessageManager.getMessage(path + ".lore." + alphabet, false).equals("")) {
				break;
			}

			builder.addLoreLine(path + ".lore." + alphabet);
		}

		return builder;
	}

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
