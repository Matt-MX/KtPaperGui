package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.signal.signal
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.dsl.signalButton
import com.mattmx.ktgui.utils.not
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryType

class SignalExample : Example {
    val gui = gui(!"Signals", InventoryType.HOPPER) {
        val list = Bukkit.getOnlinePlayers().map { it.name }
        var listIndex by signal(0)

        signalButton(Material.KNOWLEDGE_BOOK) {
            named(!"&7'&f${list[listIndex]}&7'")
            lore {
                add(!"&7Char length: &f${list[listIndex].length}")
            }
        } slot 2

        signalButton(Material.LIME_DYE) {
            named(!"&a&l[CLICK]")
            lore {
                add(!"&7To change to the next username!")
            }
            click {
                ClickType.LEFT {
                    listIndex++
                }
            }
        } slot 4
    }

    override fun run(player: Player) = gui.open(player)
}