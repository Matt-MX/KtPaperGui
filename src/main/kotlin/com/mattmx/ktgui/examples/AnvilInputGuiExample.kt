package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.utils.Chat
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.AnvilInventory

class AnvilInputGuiExample : GuiScreen("Rename GUI", type = InventoryType.ANVIL) {
    init {
        GuiButton()
            .lore {
                add(Chat.color("Using the anvil rename feature"))
            } material Material.PAPER named "&aRename this item" slot 0 childOf this
        GuiButton()
            .click {
                generic = { e ->
                    val player = e.whoClicked as Player
                    val stack = player.openInventory.getSlotType(2)
                    forceClose(player)
                    player.sendMessage(Chat.color(stack.name))
                }
            }.lore {
                add("&7Click to finish!")
            } childOf this slot 2 named "&aDone" material Material.PAPER
    }
}