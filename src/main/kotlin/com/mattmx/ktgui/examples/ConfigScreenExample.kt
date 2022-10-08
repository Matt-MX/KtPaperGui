package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.*
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.item.ItemBuilder
import com.mattmx.ktgui.utils.Chat
import org.bukkit.Material

class ConfigScreenExample : GuiScreen("Example Config", 3) {
    init {
        // Create ItemStack states for GuiToggleButtons
        val enabled = ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&aEnabled")
        val disabled = ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("&cDisabled")
        var slot = 12
        repeat(2) {
            /**
             * With a toggle button, it can have two states: on and off.
             * Provide an ItemStack state for both states. You can also add
             * a callback for when the state it changed.
             */
            GuiToggleButton(
                enabled.copy().lore("&8This is item $it").make(),
                disabled.copy().lore("&8This is item $it").make(),
                false
            ) { _, e, s ->
                e?.whoClicked?.sendMessage(Chat.color("&cChanged button $it! ($s)"))
            } childOf this slot slot
            slot += 2
        }
        /**
         * Sometimes you may need to allow a user to cycle through multiple values
         * to select one. This button allows you to do that using an ID and an ItemStack.
         * You can provide a callback for when the value is changed.
         *
         * Provide a MutableMap<String, ItemStack> on initialization.
         */
        GuiCycleButton(
            map = mutableMapOf(
                "dirt" to ItemBuilder(Material.DIRT).name("&6Dirt").lore("&8Click to cycle").make(),
                "grass_block" to ItemBuilder(Material.GRASS_BLOCK).lore("&8Click to cycle").name("&6Grass Block")
                    .make(),
                "diamond" to ItemBuilder(Material.DIAMOND_BLOCK).lore("&8Click to cycle").name("&bDiamond Block").make()
            )
        ) childOf this slot 10
        /**
         * You may want to allow the user to read all options instead
         * of having to cycle through them all to find them.
         * We can achieve this by using Item lore. Right clicking and Left clicking
         * to cycle through the options.
         *
         * You MUST call .specialLore {} and provide some lore for it. !!(Do not call .lore)!!
         * You can add non-selectable lore by not specifying an ID for a lore entry.
         *
         * Multiple lore entries may share the same ID.
         * You can also listen for changes with a callback.
         */
        NumberWidgetButton()
            .lore {
                add("&8Right click to increase")
                add("&8Left click to decrease")
            } material Material.BLUE_STAINED_GLASS_PANE named "&9Amount widget" childOf this slot 16
        LoreCycleButton()
            .specialLore {
                addLore {
                    id = "1"
                    line = "&8 ⤷ Option one"
                    lineSelected = "&7 ⤷ &aOption one"
                }
                addLore {
                    id = "2"
                    line = "&8 ⤷ Option two"
                    lineSelected = "&7 ⤷ &aOption two"
                }
                addLore {
                    line = "&8&oThis line is not selectable."
                }
                addLore {
                    id = "3"
                    line = " &8⤷ Option three"
                    lineSelected = "&7 ⤷ &aOption three"
                }
                addLore {
                    id = "3"
                    line = "  &8 ⤷ Description of option blah blah"
                    lineSelected = "  &7 ⤷ Description of option blah blah"
                }
            } material Material.PAPER named "&d&lLore Cycle option" childOf this slot 13
    }
}
