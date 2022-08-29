package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.components.button.IGuiButton
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

interface IGuiScreen {

    fun onOpen(event: InventoryOpenEvent, player: Player) {}

    fun getSlots(button: IGuiButton) : List<Int>

    fun totalSlots() : Int

    fun size() : Int { return 0 }

    fun setSlot(button: IGuiButton, slot: Int) : IGuiScreen

    fun createCopyAndOpen(player: Player) : IGuiScreen

    fun open(player: Player)

    fun copyAndFormat(player: Player) : IGuiScreen

    fun copy() : IGuiScreen

    fun destroy() {}

    fun addChild(child: IGuiButton)

    /**
     * Will be called if a player with this gui
     * clicks while this gui is open
     */
    fun click(e: InventoryClickEvent)

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
     * sends a chat message
     */
    fun chat(e: AsyncPlayerChatEvent)

    /**
     * Will be called if a player with this gui
     * tries to move
     */
    fun move(e: PlayerMoveEvent)
}