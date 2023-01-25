package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.GuiPattern
import com.mattmx.ktgui.components.applyPattern
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.extensions.color
import com.mattmx.ktgui.item.itemBuilderStack
import org.bukkit.Bukkit
import org.bukkit.Material
import java.util.*

object GuiPatternExample {
    val gui = gui<GuiScreen> {
        rows = 3
        title = "Pattern Example"

        val pattern = GuiPattern("""
            xxxxxxxxx
            -a-b-c-d-
            xxxxxxxxx
        """.trimIndent())

        pattern['x'] = button<GuiButton> {
            material(Material.BLUE_STAINED_GLASS_PANE)
            named("")
        }
        pattern['a'] = button<GuiButton> {
            item = itemBuilderStack {
                material = Material.PLAYER_HEAD
                name = "&5MattMX"
                skullOwner = Bukkit.getOfflinePlayer(UUID.fromString("5fb2e3a2-173b-45e9-a37b-186d40164114"))
                format { color() }
            }
        }
        pattern['b'] = button<GuiButton> {
            named("&7Something")
            material(Material.PAPER)
        }
        pattern['c'] = button<GuiButton> {
            named("&7Something else")
            material(Material.COMPARATOR)
        }
        pattern['d'] = button<GuiButton> {
            named("&7Meow")
            material(Material.TROPICAL_FISH)
        }

        applyPattern(pattern)
    }
}