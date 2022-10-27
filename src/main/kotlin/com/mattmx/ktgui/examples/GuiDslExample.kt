package com.mattmx.ktgui.examples

import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import org.bukkit.Material
import org.bukkit.entity.Player

object GuiDslExample {

    val gui = gui {
        title = "DSL Gui example"
        rows = 1
        button(this) {
            material(Material.PAPER)
            named("&cThis GUI was built using Kotlin's DSL.")
            lore {
                add("&7For more information on building GUIs using DSL")
                add("&7check the GitHub wiki on KtBukkitGui.")
            }
            slot(4)
        }
        button(this) {
            material(Material.BARRIER)
            named("&c&lClose the GUI")
            lore {
                add("&7Click me to close the GUI.")
            }
            click {
                generic = { forceClose(it.whoClicked as Player) }
            }
            slot(8)
        }
    }

    fun open(player: Player) {
        gui.openAndFormat(player)
    }

}