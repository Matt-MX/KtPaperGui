package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.*
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.item.itemBuilder
import com.mattmx.ktgui.scheduling.not
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class ConfigScreenExample : GuiScreen(!"Example Config", 3), Example {
    init {
        // Create ItemStack states for GuiToggleButtons
        val enabled = itemBuilder(Material.LIME_STAINED_GLASS_PANE).name(!"&aEnabled")
        val disabled = itemBuilder(Material.RED_STAINED_GLASS_PANE).name(!"&cDisabled")
        var slot = 12
        repeat(2) {
            /**
             * With a toggle button, it can have two states: on and off.
             * Provide an ItemStack state for both states. You can also add
             * a callback for when the state it changed.
             */
            GuiToggleButton(
                enabled.copy().lore(!"&8This is item $it").build(),
                disabled.copy().lore(!"&8This is item $it").build())
                .enabledOnDefault(true)
                .changeWithClickType(ClickType.LEFT)
                .changed {
                    player.sendMessage(!"&cChanged button ${it}! (${button.enabled()})")
                } slot slot childOf this
            slot += 2
        }
        /**
         * Sometimes you may need to allow a user to cycle through multiple values
         * to select one. This button allows you to do that using an ID and an ItemStack.
         * You can provide a callback for when the value is changed.
         *
         * Provide a MutableMap<String, ItemStack> on initialization.
         */
        GuiCycleButton()
            .set("dirt", itemBuilder(Material.DIRT).name(!"&6Dirt").lore(!"&8Click to cycle").build())
            .set("grass_block", itemBuilder(Material.GRASS_BLOCK).lore(!"&8Click to cycle").name(!"&6Grass Block").build())
            .set("diamond", itemBuilder(Material.DIAMOND_BLOCK).lore(!"&8Click to cycle").name(!"&bDiamond Block").build())
            .withDefaultClickEvents()
            .changed {
                player.sendMessage(!"&7You changed to ${button.selectedValue}")
            } childOf this slot 10
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
                add(!"&8Right click to increase")
                add(!"&8Left click to decrease")
            } material Material.BLUE_STAINED_GLASS_PANE named !"&9Amount widget" childOf this slot 16

        LoreCycleButton()
            .loreCycle {
                add(!"&8 ⤷ Option one" withId 0 withSelectedText !"&7 ⤷ &aOption one")
                add(!"&8 ⤷ Option two" withId 1 withSelectedText !"&7 ⤷ &aOption two")
                add(!"&8&oThis line is not selectable.")
                add(!"&8 ⤷ Option three" withId 2 withSelectedText !"&7 ⤷ &aOption three")
                add(!"  &8 ⤷ Description of option blah blah" withId 2 withSelectedText !"  &7 ⤷ Description of option blah blah")
            }.click {
                ClickType.LEFT {
                    button.selected++
                }
                ClickType.RIGHT {
                    button.selected--
                }
            } material Material.PAPER named !"&d&lLore Cycle option" childOf this slot 13
    }

    override fun run(player: Player) = open(player)
}
