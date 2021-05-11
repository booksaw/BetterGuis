package com.booksaw.betterGuis.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.booksaw.betterGuis.BetterGuis;
import com.booksaw.betterGuis.message.MessageManager;

import me.clip.placeholderapi.PlaceholderAPI;

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

	public static ItemStack replacePlaceholders(ItemStack item, Player p) {

		if (!BetterGuis.placeholderAPI) {
			return item;
		}

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(PlaceholderAPI.setPlaceholders(p, meta.getDisplayName()));

		if (meta.getLore() != null) {
			List<String> lore = new ArrayList<>();
			for (String str : meta.getLore()) {
				lore.add(PlaceholderAPI.setPlaceholders(p, str));
			}
			meta.setLore(lore);
		}

		item.setItemMeta(meta);

		return item;
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
