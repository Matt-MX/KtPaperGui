package com.mattmx.ktguis.components

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

interface IGuiButton {
    /**
     * @return the ItemStack we want to display.
     */
    fun getItemStack() : ItemStack?

    /**
     * Called when this button is clicked
     */
    fun thisClicked(e: InventoryClickEvent)

    /**
     * Called when something else in the gui is clicked
     */
    fun notClicked(e: InventoryClickEvent)
}