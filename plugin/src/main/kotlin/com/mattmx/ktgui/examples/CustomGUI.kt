package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.components.button.LegacyGuiToggleButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.item.itemBuilder
import com.mattmx.ktgui.utils.not
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import kotlin.random.Random

class CustomGUI : GuiScreen(!"&8&l⤷ &#7f52ffK&#984fd8t&#b14bb1G&#c94889u&#e24462i &8» &#7f52ffExample GUI", 4), Example {
    init {
        /**
         * Thanks to an ItemBuilder, we can create skull objects with a
         * player skull skin!
         */
        val skull = itemBuilder(Material.PLAYER_HEAD)
            .skull(Bukkit.getOfflinePlayer("MattMX"))
            .name(!"&#7F52FF&lMattMX")
            .lore(!"&8Hey! I'm the creator of KtGui.")
            .lore(!"&8I hope you're finding this library")
            .lore(!"&8easy-to-use and enjoying the features!")
            .lore(!"&#E24462&nhttps://mattmx.com/")

        GuiButton()
            .click {
                any { player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f ,1f) }
            } fromItemBuilder  skull childOf this slot 27
        /**
         * Fill the bottom slots with gray stained glass panes.
         */
        GuiButton(Material.GRAY_STAINED_GLASS_PANE) slots (28..34).toList() named !" " childOf this
        /**
         * We can also easily make close buttons with just a few lines of code.
         */
        GuiButton()
            .click {
                any { forceClose(player) }
            }.lore {
                add(!"&8Click to close the Gui Interface")
            }.materialOf("barrier", Material.DIRT) named !"&c&lClose" slot 35 childOf this
        /**
         * The GuiToggleButton is an example of what you can accomplish with this library.
         * We can provide an item state for when the button is enabled, and one for when it's disabled.
         * We can also provide a callback for when a button is pressed.
         */
        LegacyGuiToggleButton(
            itemBuilder(Material.LIME_STAINED_GLASS_PANE).name(!"&aEnabled").lore(!"&8Click to toggle").build(),
            itemBuilder(Material.RED_STAINED_GLASS_PANE).name(!"&cDisabled").lore(!"&8Click to toggle").build(),
        ).onChange {
            player.sendMessage(!"&8&l⤷ &#7f52ffToggled thing to ${button.enabled()}")
        } slot 10 childOf this
        /**
         * Random example of using callbacks to do different things
         */
        GuiButton()
            .click {
                any {
                    repeat(3) {
                        val offsetX = Random.nextDouble(-2.0, 2.0) * 2
                        val offsetZ = Random.nextDouble(-2.0, 2.0) * 2
                        player.spawnParticle(Particle.FIREWORKS_SPARK, player.location.add(offsetX, 1.0, offsetZ), 20)
                    }
                }
                ClickType.DROP {
                    player.sendMessage("&8&l⤷ &#7f52ffWhat a party pooper. :(")
                }
            }.lore {
                add(!"&8Party time!")
            } named !"&#7F52FF&lWhat does this button do?" material Material.FIREWORK_ROCKET slot 13 childOf this
        /**
         * A final example of buttons for callback.
         */
        GuiButton()
            .click {
                any {
                    val world = player.world
                    world.time += 12000
                    player.sendMessage(!"&8&l⤷ &#7f52ffTime +12000 game ticks!")
                }
            }.lore {
                add(!"&8Click to change the time of day by 12000 game ticks")
            } material Material.CLOCK named !"&#7F52FF&lTime switcher" slot 16 childOf this
    }

    override fun run(player: Player) = open(player)
}