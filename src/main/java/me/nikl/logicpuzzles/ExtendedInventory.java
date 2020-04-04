package me.nikl.logicpuzzles;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ExtendedInventory {
    private Inventory upperInventory;
    private Inventory lowerInventory;

    public ExtendedInventory(Inventory upperInventory, Inventory lowerInventory) {
        this.upperInventory = upperInventory;
        this.lowerInventory = lowerInventory;
    }

    public void setItem(int slot, ItemStack item) {
        if (slot >= upperInventory.getSize()) {
            lowerInventory.setItem(slot - upperInventory.getSize() + 9, item);
        } else {
            upperInventory.setItem(slot, item);
        }
    }
}
