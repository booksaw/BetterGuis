package com.booksaw.betterGuis.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.inventory.ClickType;

import com.booksaw.betterGuis.gui.BetterGui;
import com.booksaw.betterGuis.item.BetterItem;
import com.booksaw.guiAPI.API.items.itemActions.GuiEvent;

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
	 * Used to
	 */
	private List<ClickType> type = new ArrayList<>();;

	/**
	 * Used to execute the action, called whenver a trigger is activated
	 * 
	 * @param gui The gui which the item was placed in
	 * @param e   The event called which triggered this execution
	 */
	public abstract void execute(BetterGui gui, GuiEvent e);

	public List<ClickType> getType() {
		return type;
	}

	/**
	 * Used to set the click types for this item
	 * 
	 * @param item The item this trigger is associated with (for saving the changed
	 *             details)
	 * @param type The list of click types to add
	 */
	public void setType(BetterItem item, List<ClickType> type) {
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
	public void addType(BetterItem item, ClickType type) {
		this.type.add(type);
		item.changes();
	}

	/**
	 * Used to remove a click type from the existing list
	 * 
	 * @param item
	 * @param type
	 */
	public void removeType(BetterItem item, ClickType type) {
		this.type.remove(type);
		item.changes();
	}

	/**
	 * Used to check if the click type should activate this object 
	 * @param type The click type to check
	 * @return If the trigger has been activated
	 */
	public boolean isType(ClickType type) {
		return this.type.contains(type);
	}

}
