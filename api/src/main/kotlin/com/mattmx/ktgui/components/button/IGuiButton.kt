package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

interface IGuiButton {
    /**
     * @return the ItemStack we want to display.
     */
    fun getItemStack() : ItemStack?

    /**
     * Called when this button is clicked
     */
    fun onButtonClick(e: ButtonClickedEvent<*>)

    fun onButtonDrag(e: InventoryDragEvent)

    fun formatIntoItemStack(player: Player? = null) : ItemStack?

    fun copy(parent: IGuiScreen) : IGuiButton

    infix fun slot(slot: Int) : IGuiButton

    infix fun slots(slots: List<Int>) : IGuiButton

    fun slots() : List<Int>?

    infix fun childOf(parent: IGuiScreen) : IGuiButton

    fun destroy()
}