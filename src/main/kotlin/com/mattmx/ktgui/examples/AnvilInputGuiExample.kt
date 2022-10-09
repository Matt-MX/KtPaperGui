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
            .click {
                generic = { e ->
                    val player = e.whoClicked as Player
                    val inv = player.openInventory as AnvilInventory
                    forceClose(player)
                    player.sendMessage(Chat.color(inv.renameText ?: "nothing"))
                }
            }.lore {
                add("&7Click to finish!")
            } childOf this slot 2 named "&aDone" material Material.PAPER
    }
}