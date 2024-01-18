package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.GuiPattern
import com.mattmx.ktgui.components.applyPattern
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.item.itemBuilderStack
import com.mattmx.ktgui.utils.not
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.*

class GuiPatternExample : Example {
    val gui = gui(!"Pattern Example", 3) {

        val pattern = GuiPattern("""
            xxxxxxxxx
            -a-b-c-d-
            xxxxxxxxx
        """.trimIndent())

        pattern['x'] = button(Material.BLUE_STAINED_GLASS_PANE) {
            named(!"")
        }
        pattern['a'] = button(Material.PLAYER_HEAD) {
            item = itemBuilderStack(Material.PLAYER_HEAD) {
                name = !"&5MattMX"
                skullOwner = Bukkit.getOfflinePlayer(UUID.fromString("5fb2e3a2-173b-45e9-a37b-186d40164114"))
            }
        }
        pattern['b'] = button(Material.PAPER) {
            named(!"&7Something")
        }
        pattern['c'] = button(Material.COMPARATOR) {
            named(!"&7Something else")
        }
        pattern['d'] = button(Material.TROPICAL_FISH) {
            named(!"&7Meow")

            click {
                ClickType.LEFT {
                    player.playSound(player, Sound.ENTITY_CAT_PURREOW, 1f, 1f)
                }
            }
        }

        applyPattern(pattern)
    }

    override fun run(player: Player) = gui.open(player)
}