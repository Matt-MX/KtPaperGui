package com.mattmx.ktgui.examples

import com.mattmx.ktgui.KotlinBukkitGui
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.utils.Chat
import com.mattmx.ktgui.ymlguis.YamlGuiParser
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import java.util.stream.IntStream

object DynamicExample {
    /**
     * An example of making more dynamic GUIs as objects instead of a set class.
     * I don't personally recommend this but it shouldn't matter too much.
     */
    fun serverChangerExample(player: Player) {
        /**
         * Create a GuiScreen object dynamically
         */
        val gui = GuiScreen("Oops this name is bad")
        /**
         * Make sure to specify title and rows.
         * (You can use the constructor but here we're showing infix usage.
         */
        gui title "Server changer" rows 3
        val generic = { e: InventoryClickEvent -> e.whoClicked.sendMessage(Chat.format("&8&l⤷ &#7f52ffThis button can do stuff")) }
        /**
         * Example of how to fill a GUI with items
         */
        GuiButton() material Material.WHITE_STAINED_GLASS_PANE slots IntStream.rangeClosed(0, gui.totalSlots() - 1).boxed().toList() childOf gui named " "
        /**
         * Creating a gui widgets and registering them
         */
        GuiButton()
            .click {
                it.generic = generic
            }.lore {
                it.add("&8&l⤷&8Click to join &7FFA!")
                it.add("&8&l⤷&812312313 players currently connected.")
            } named "&8&l⤷ &#E24462&lFFA" materialOf "diamond sword" slot 11 childOf gui
        GuiButton()
            .click {
                it.generic = generic
            }.lore {
                it.add("&8&l⤷&8Click to join &7Factions!")
                it.add("&8&l⤷&812312313 players currently connected.")
            } named "&8&l⤷ &#E24462&lFactions" material Material.TNT slot 13 childOf gui
        GuiButton()
            .click {
                it.generic = generic
            }.lore {
                it.add("&8&l⤷&8Click to join &7Survival!")
                it.add("&8&l⤷&812312313 players currently connected.")
            } named "&8&l⤷ &#E24462&lSurvival" material Material.GRASS_BLOCK slot 15 childOf gui
        GuiButton()
            .click {
                it.generic = { e -> gui.forceClose(e.whoClicked as Player) }
            }.lore {
                it.add("&8&l⤷&8Click to close the server selector")
            } named "&8&l⤷ &cClose" material Material.BARRIER slot 26 childOf gui
        /**
         * Finally, format the GUI items and display it to the player
         */
        gui.openAndFormat(player)
    }

    /**
     * Example of how we can make guis of different interfaces
     * that Minecraft provides. Simple provide a type instead of rows.
     *
     * For slot values, I would refer to: https://wiki.vg/Inventory
     */
    fun furnaceInventoryExample(player: Player) {
        val gui = GuiScreen("&cCustom furnace")
        // Set the Gui Type to a furnace.
        gui type InventoryType.FURNACE
        GuiButton()
            .click {
                it.generic = { e -> (e.whoClicked as Player).playSound(e.whoClicked.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f) }
            }.lore { it.add("&8Click me for a sound") } named "&c:3" childOf gui slot 2
        gui.openAndFormat(player)
    }

    fun poorYamlExample(player: Player) {
        YamlGuiParser.parseGuiScreen(KotlinBukkitGui.plugin!!.config.getConfigurationSection("test")!!)?.openAndFormat(player)
    }
}
