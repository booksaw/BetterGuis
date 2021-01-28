package com.booksaw.betterGuis.api;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;

import com.booksaw.betterGuis.gui.BetterGui;
import com.booksaw.betterGuis.internalApi.TypeList;
import com.booksaw.betterGuis.item.BetterItem;
import com.booksaw.guiAPI.API.items.itemActions.GuiEvent;

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
	 * Adding all internal triggers
	 */
	static {
		// TODO
	}

	private final static TypeList<Trigger> triggers = new TypeList<>();

	/**
	 * Used to register a trigger so it can be used and loaded from
	 * 
	 * @param reference    The reference for the trigger
	 * @param triggerClass The class which the trigger details are stored in
	 */
	public final static void registerTrigger(String reference, Class<? extends Trigger> triggerClass) {
		triggers.add(reference, triggerClass);
	}

	/**
	 * Used to remove a trigger from the list of available triggers
	 * 
	 * @param reference The reference for the trigger to be removed
	 */
	public final static void unregisterTrigger(String reference) {
		triggers.remove(reference);
	}

	/**
	 * Used to get an instance of a trigger subclass, any errors caused by returning
	 * null should be handled by wherever runs this method
	 * 
	 * @param reference The reference for the trigger to create
	 * @return The created trigger or null if it could not be created
	 */
	public final static Trigger getTriggerInstance(String reference) {

		if (!triggers.containsKey(reference)) {
			return null;
		}

		Trigger t;

		try {
			t = triggers.createInstance(reference);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NullPointerException e) {
			// debug output
			Bukkit.getLogger().warning("[BetterGuis] Something went wrong when loading the trigger " + reference
					+ ", the error message is below");
			e.printStackTrace();
			return null;
		}

		return t;

	}

	/**
	 * Used to store all the click types of the list
	 */
	private List<ClickType> type = new ArrayList<>();;

	public final List<ClickType> getType() {
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
	 * Used to add a single click type to the already eixsting list
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
	protected abstract String getPlaintext();

}
