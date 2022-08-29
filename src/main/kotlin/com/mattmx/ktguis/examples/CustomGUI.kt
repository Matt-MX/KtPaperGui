package com.mattmx.ktguis.examples

import com.mattmx.ktguis.components.GuiButton
import com.mattmx.ktguis.components.GuiScreen
import com.mattmx.ktguis.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class CustomGUI : GuiScreen("&9&lExample GUI") {
    /**
     * Example usage of the generic gui button creation method.
     * This should be used for specific buttons that do stuff.
     * Remember: Buttons can be reused as long as you redefine the parent!
     */
    val buttonExample = GuiButton()
        .click {
            it.generic = { e ->
                e.whoClicked.sendMessage("You clicked this button")
            }
        }.enchant {
            it[Enchantment.DAMAGE_ALL] = 10
            it[Enchantment.FIRE_ASPECT] = 2
        } materialOf "stone" childOf this

    /**
     * Example usage of if you wanted to use an ItemBuilder to create a GuiButton.
     * This would be used for more common buttons, like filling a GUI with frames.
     */
    val builder = ItemBuilder(Material.DIAMOND_SWORD)
        .enchantment(Enchantment.FIRE_ASPECT, 2)
        .name("&chello world")
        .lore("&9trollage")

    val anotherExample = GuiButton()
        .click {
            it.generic = { e -> e.whoClicked.sendMessage("You clicked me awww :33333") }
        } ofBuilder builder childOf this
}