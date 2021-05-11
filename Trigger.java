package com.booksaw.betterGuis.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import com.booksaw.betterGuis.BetterGuis;
import com.booksaw.betterGuis.gui.BetterGui;
import com.booksaw.betterGuis.gui.editor.actions.DetailsChangeAction;
import com.booksaw.betterGuis.item.BetterItem;
import com.booksaw.betterGuis.message.MessageManager;
import com.booksaw.guiAPI.API.builder.ItemBuilder;
import com.booksaw.guiAPI.API.items.itemActions.GuiEvent;
import com.booksaw.guiAPI.API.items.itemActions.ItemAction;
import com.booksaw.guiAPI.API.items.itemActions.LongChatEvent;

import net.md_5.bungee.api.ChatColor;

/**
 * A trigger is an action which is run when a user does a specific type of click
 * in the inventory A trigger can have multiple activations
 * 
 *
 * <h1>ENSURE THAT ANY CHANGES MADE TO THIS ITEM RUN THE METHOD
 * BetterItem.changes() ELSE BETTERGUIS WILL NOT KNOW TO SAVE THE NEW VALUE TO
 * THE CONFIG</h1>
 * <p>
 * This class and any subclasses should not store the object for the BetterItem,
 * and instead should take it as a parameter. This is done so the manual garbage
 * collector used to unload items which have not been used recently will not
 * work properly and this item will remain in a semi-loaded form, needlessly
 * wasting server resources
 * </p>
 * 
 * 
 * @author James McNair
 *
 */
public abstract class Trigger {

	/**
	 * Used to check if the click type provided is a considered valid by the plugin
	 * 
	 * @param type the type to check
	 * @return If it is a supported click type
	 */
	public static boolean isValid(ClickType type) {
		if (type == null || type == ClickType.CREATIVE || type == ClickType.WINDOW_BORDER_LEFT
				|| type == ClickType.WINDOW_BORDER_RIGHT || type == ClickType.UNKNOWN
				|| type == ClickType.DOUBLE_CLICK) {
			return false;
		}
		return true;
	}

	/**
	 * Used to store all the click types of the list
	 */
	private List<ClickType> type = new ArrayList<>();;

	public final List<ClickType> getClickTypes() {
		return type;
	}

	/**
	 * Used to set the click types for this item
	 * 
	 * @param item The item this trigger is associated with (for saving the changed
	 *             details)
	 * @param type The list of click types to add
	 */
	public final void setType(BetterItem item, List<ClickType> type) {
		this.type = type;
		item.changes();
	}

	/**
	 * Used to add a single click type to the already existing list
	 * 
	 * @param item The item this trigger is associated with (for saving the changed
	 *             details)
	 * @param type The click type to add
	 */
	public final void addType(BetterItem item, ClickType type) {
		this.type.add(type);
		item.changes();
	}

	/**
	 * Used to remove a click type from the existing list
	 * 
	 * @param item
	 * @param type
	 */
	public final void removeType(BetterItem item, ClickType type) {
		this.type.remove(type);
		item.changes();
	}

	/**
	 * Used to check if the click type should activate this object
	 * 
	 * @param type The click type to check
	 * @return If the trigger has been activated
	 */
	public final boolean isType(ClickType type) {
		return this.type.contains(type);
	}

	/**
	 * Used to save the details for this trigger to file
	 * 
	 * @param config The config section to save the details to
	 */
	public final void save(ConfigurationSection config) {
		config.set("type", getReference());

		// saving the click type information
		List<String> types = new ArrayList<>();
		for (ClickType type : type) {
			types.add(type.toString());
		}

		config.set("clickType", types);

		saveDetails(config);
	}

	public final void load(ConfigurationSection config) {
		// saving the click type information
		List<String> types = config.getStringList("clickType");
		for (String type : types) {
			this.type.add(ClickType.valueOf(type));
		}

		loadDetails(config);
	}

	protected BetterGuis main;

	public void setMain(BetterGuis main) {
		this.main = main;
	}

	/**
	 * @return the item to represent this trigger in a gui
	 */
	public ItemStack getItem() {
		return getItem(true);
	}

	/**
	 * 
	 * @param includeDetails If speicifc trigger details should be included (if the
	 *                       item should just be a generic trigger or not)
	 * @return The item to represent this trigger in a gui
	 */
	public ItemStack getItem(boolean includeDetails) {
		ItemBuilder builder = new ItemBuilder(getMaterial()).setName(ChatColor.GOLD + "" + ChatColor.BOLD
				+ getReference().substring(0, 1).toUpperCase() + getReference().substring(1))
				.setLore(getDetails(includeDetails));
		return builder.getItem();

	}

	public List<String> getDetails(boolean includeDetails) {
		List<String> toReturn = new ArrayList<>();
		toReturn.add(ChatColor.AQUA + getHelp());

		if (requiresDetails() && includeDetails) {
			toReturn.add(String.format(MessageManager.getMessage("detailssyntax"), getPlaintext()));
		}

		toReturn.add(String.format(MessageManager.getMessage("triggerseditor.syntax"),
				((getParameters() != null) ? getParameters() : "None")));

		return toReturn;
	}

	/**
	 * Used to get the item action to edit this type of trigger
	 * <p>
	 * This defaults to just a chat prompt, this is only here if a trigger wants a
	 * different style of event
	 * </p>
	 * 
	 * @parm item The item associated with this trigger
	 * @return The item action for this trigger
	 */
	public ItemAction getItemAction(BetterItem item) {
		return new LongChatEvent(new DetailsChangeAction(item, this));
	}

	@Override
	public String toString() {
		return ChatColor.AQUA + getReference() + ChatColor.WHITE + " - " + ChatColor.GOLD + getPlaintext();
	}

	/**
	 * Used to execute the action, called whenever a trigger is activated
	 * 
	 * @param gui The gui which the item was placed in
	 * @param e   The event called which triggered this execution
	 */
	public abstract void execute(BetterGui gui, GuiEvent e);

	/**
	 * @return the reference for this trigger, this reference should never change as
	 *         it is used for saving
	 */
	public abstract String getReference();

	/**
	 * Used to save the specific subclass details for this trigger
	 * 
	 * @param section The config section to save the details to
	 */
	protected abstract void saveDetails(ConfigurationSection section);

	/**
	 * Used to load the specific subclass details for this trigger
	 * 
	 * @param section The config section which stores all the details
	 */
	protected abstract void loadDetails(ConfigurationSection section);

	/**
	 * @return A plaintext description of this specific trigger, this is used in
	 *         /bgui item details
	 */
	public abstract String getPlaintext();

	/**
	 * Used to get the help message for how the trigger details should be formatted
	 * 
	 * @return
	 */
	public abstract String getHelp();

	/**
	 * @return The parameters required for this trigger
	 */
	public abstract String getParameters();

	/**
	 * Used to load a new trigger with no details (from a command)
	 * 
	 * @return If the trigger can accept no details as an option
	 */
	public abstract boolean loadFromString();

	/**
	 * Used to load details about the trigger from a string (from a command)
	 * 
	 * @param args The details provded
	 * @return If the details provided are valid
	 */
	public abstract boolean loadFromString(String args);

	/**
	 * Used to determine if the trigger requires details
	 * 
	 * @return True - the trigger requires details. False - The trigger does not
	 *         require details
	 */
	public abstract boolean requiresDetails();

	/**
	 * @return The material that should represent this trigger in any guis
	 */
	public abstract Material getMaterial();

}
