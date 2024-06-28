package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.components.button.IGuiButton
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

interface IGuiScreen {

    fun getSlots(button: IGuiButton<*>): List<Int>

    fun totalSlots(): Int

    fun numberOfItems(): Int {
        return 0
    }

    fun setSlot(button: IGuiButton<*>, slot: Int): IGuiScreen

    fun clearSlot(vararg slot: Int)

    fun open(player: Player)

    fun copy(): IGuiScreen

    fun destroy() {}

    fun addChild(child: IGuiButton<*>): IGuiScreen

    /**
     * Will be called if a player with this gui
     * clicks while this gui is open
     */
    fun click(e: InventoryClickEvent)

    fun drag(e: InventoryDragEvent)

    /**
     * Will be called if a player with this gui
     * closes the gui window
     */
    fun close(e: InventoryCloseEvent)

    /**
     * Will be called if a player with this gui
     * leaves the game
     */
    fun quit(e: PlayerQuitEvent)

    /**
     * Will be called if a player with this gui
     * tries to move
     */
    fun move(e: PlayerMoveEvent)
}