package com.mattmx.ktgui.examples

import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.scheduling.not
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryType

class TitleCounterExample : Example {
    val gui = gui(!"Counter (0)", InventoryType.HOPPER) {
        var counter = 0
        button(Material.GRAY_DYE) {
            named(!"&f&lButton!")
            lore {
                add(!"&c- &7Right click")
                add(!"&a+ &7Left click")
            }
            click {
                ClickType.LEFT {
                    counter++
                    title = !"Counter ($counter)"
                }
                ClickType.RIGHT {
                    counter--
                    title = !"Counter ($counter)"
                }
            }
        } slot 2
    }

    override fun run(player: Player) = gui.open(player)
}