package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

object GuiBuilderExample {
    /**
     * An example of making more dynamic GUIs as objects instead of a set class.
     * I don't personally recommend this but it shouldn't matter too much.
     */
    fun buildAndOpen(player: Player) {
        // Create a GUI Builder object with a title of "Test Gui"
        val gui = GuiScreen("Test Gui")
        // We can use infix expressions for redefining title and anything else we want
        gui title "Different title" rows 3
        // Create a gui button in slot 10, with some different click events for different click types
        GuiButton()
            .click {
                it.generic = { e -> e.whoClicked.sendMessage("I haven't added a handler for this click!") }
                it.right = { e -> e.whoClicked.sendMessage("Right clicked!") }
                it.left = { e -> e.whoClicked.sendMessage("Left clicked!") }
            }.enchant {
                it[Enchantment.DAMAGE_ALL] = 5
                it[Enchantment.DURABILITY] = 3
                it[Enchantment.MENDING] = 1
            } childOf gui material Material.DIAMOND_SWORD named "&7Click me differently" slot 10
        // Giving an example of lore and how we can create gui objects for specific players easily
        // Formatting thanks to PlaceholderAPI!
        GuiButton().lore {
            it.add("&7Lore line one")
            it.add("&8Lore line two")
            it.add("&#fb0000R&#54a911G&#00bb66B&#0038ff!")
            it.add("&7Hello, %player%!")
        } material Material.NETHERITE_AXE childOf gui slot 11 named "&c%player%&7's name"
        gui.openAndFormat(player)
    }
}
