package com.imdeity.deityapi.object;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Inventory API
 * 
 * @author vanZeben
 */
public class InventoryObject {
    
    /**
     * Adds items, and stacks them to 64
     * 
     * @param inv
     * @param item
     * @param amount
     * @return
     */
    public int addAndStackTo64(Inventory inv, ItemStack item, int amount) {
        return this.addManually(inv, item, amount, 64);
    }
    
    /**
     * Adds an item to the inventory
     * 
     * @param inv
     * @param item
     * @return
     */
    public int addItemToInventory(Inventory inv, ItemStack item) {
        int amount = item.getAmount();
        amount = amount > 0 ? amount : 1;
        ItemStack itemstack = new ItemStack(item.getType(), amount, item.getDurability());
        itemstack.addEnchantments(item.getEnchantments());
        
        HashMap<Integer, ItemStack> items = inv.addItem(new ItemStack[] { itemstack });
        amount = 0;
        ItemStack toAdd;
        for (Iterator<ItemStack> i = items.values().iterator(); i.hasNext(); amount += toAdd.getAmount()) {
            toAdd = i.next();
        }
        return amount;
    }
    
    /**
     * Manually adds the specified amount to the inventory
     * 
     * @param inv
     * @param item
     * @param amount
     * @param max
     * @return
     */
    public int addManually(Inventory inv, ItemStack item, int amount, int max) {
        if (amount <= 0) { return 0; }
        
        for (int slot = 0; (slot < inv.getSize()) && (amount > 0); slot++) {
            ItemStack curItem = inv.getItem(slot);
            ItemStack dupe = item.clone();
            
            if ((curItem == null) || (curItem.getType() == Material.AIR)) {
                dupe.setAmount(amount > max ? max : amount);
                dupe.addEnchantments(item.getEnchantments());
                amount -= dupe.getAmount();
                inv.setItem(slot, dupe);
            } else if ((this.itemEquals(item, curItem)) && (curItem.getAmount() != max)) {
                int cA = curItem.getAmount();
                int amountAdded = amount > max - cA ? max - cA : amount;
                dupe.setAmount(cA + amountAdded);
                amount -= amountAdded;
                dupe.addEnchantments(item.getEnchantments());
                inv.setItem(slot, dupe);
            }
        }
        
        return amount;
    }
    
    /**
     * Returns the amount of items in the inventory
     * 
     * @param inv
     * @param item
     * @return
     */
    public int amountOfItemsInInventory(Inventory inv, ItemStack item) {
        if (!inv.contains(item.getType())) { return 0; }
        
        int amount = 0;
        for (ItemStack i : inv.getContents()) {
            if (!this.itemEquals(i, item)) {
                continue;
            }
            amount += i.getAmount();
        }
        return amount;
    }
    
    /**
     * Returns if the specified item can fit in the inventory fully
     * 
     * @param inv
     * @param item
     * @return
     */
    public boolean canFitInInventory(Inventory inv, ItemStack item) {
        int maxStackSize = item.getType().getMaxStackSize();
        int amount = item.getAmount();
        int amountLeft = amount;
        
        for (ItemStack currentItem : inv.getContents()) {
            if (amountLeft <= 0) { return true; }
            
            if ((currentItem == null) || (currentItem.getType() == Material.AIR)) {
                amountLeft -= maxStackSize;
            } else {
                int currentAmount = currentItem.getAmount();
                
                if ((currentAmount != maxStackSize) && (this.itemEquals(currentItem, item))) {
                    amountLeft = currentAmount + amountLeft <= maxStackSize ? 0 : amountLeft - (maxStackSize - currentAmount);
                }
            }
        }
        if (amountLeft <= 0) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns if 2 items are the same
     * 
     * @param i
     * @param item
     * @return
     */
    private boolean itemEquals(ItemStack i, ItemStack item) {
        short durability = item.getDurability();
        return (i != null) && (i.getType() == item.getType()) && (i.getEnchantments().equals(item.getEnchantments())) && ((durability == -1) || (i.getDurability() == durability));
    }
    
    public int itemFitsInInventory(Inventory inv, ItemStack item) {
        int maxStackSize = item.getType().getMaxStackSize();
        int amount = item.getAmount();
        int amountLeft = amount;
        
        for (ItemStack currentItem : inv.getContents()) {
            if (amountLeft <= 0) { return 0; }
            
            if ((currentItem == null) || (currentItem.getType() == Material.AIR)) {
                amountLeft -= maxStackSize;
            } else {
                int currentAmount = currentItem.getAmount();
                
                if ((currentAmount != maxStackSize) && (this.itemEquals(currentItem, item))) {
                    amountLeft = currentAmount + amountLeft <= maxStackSize ? 0 : amountLeft - (maxStackSize - currentAmount);
                }
            }
        }
        return amountLeft;
    }
    
    /**
     * Removes an item from the inventory
     * 
     * @param inv
     * @param item
     * @return
     */
    public int removeItemFromInventory(Inventory inv, ItemStack item) {
        int amount = item.getAmount();
        amount = amount > 0 ? amount : 1;
        Material itemMaterial = item.getType();
        
        int first = inv.first(itemMaterial);
        if (first == -1) { return amount; }
        
        for (int slot = first; slot < inv.getSize(); slot++) {
            if (amount <= 0) { return 0; }
            ItemStack currentItem = inv.getItem(slot);
            if ((currentItem == null) || (currentItem.getType() == Material.AIR)) {
                continue;
            }
            if (this.itemEquals(currentItem, item)) {
                int currentAmount = currentItem.getAmount();
                if (amount == currentAmount) {
                    currentItem = null;
                    amount = 0;
                } else if (amount < currentAmount) {
                    currentItem.setAmount(currentAmount - amount);
                    amount = 0;
                } else {
                    currentItem = null;
                    amount -= currentAmount;
                }
                inv.setItem(slot, currentItem);
            }
        }
        return amount;
    }
}
