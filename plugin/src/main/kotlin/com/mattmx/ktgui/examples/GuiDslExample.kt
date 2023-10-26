package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.entity.Player

object GuiDslExample {

    val gui = gui<GuiScreen> {
        title = "DSL Gui example"
        rows = 1

        button<GuiButton> {
            material(Material.PAPER)
            named("&cThis GUI was built using Kotlin's DSL.")
            lore {
                add("&7For more information on building GUIs using DSL")
                add("&7check the GitHub wiki on KtBukkitGui.")
                add("&eClick me!")
            }
            click {
                any {
                    player.sendMessage(!"&c:3 meow")
                }
            }
            slot(4)
        }
        button<GuiButton> {
            material(Material.BARRIER)
            named("&c&lClose the GUI")
            lore {
                add("&7Click me to close the GUI.")
            }
            click {
                any { forceClose(player) }
            }
            slot(8)
        }
    }

    fun open(player: Player) {
        gui.openAndFormat(player)
        println(gui.items)
    }

}