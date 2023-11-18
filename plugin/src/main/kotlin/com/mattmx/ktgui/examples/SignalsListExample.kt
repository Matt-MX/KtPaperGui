package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.signal.signal
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.dsl.signalButton
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryType
import java.util.UUID

class SignalsListExample : Example {
    val gui = gui(!"Signals (List)", InventoryType.HOPPER) {
        val list = signal<Any, ArrayList<String>>(arrayListOf())

        signalButton(Material.KNOWLEDGE_BOOK) {
            named(!"&7This list has &f${list().size} items:")
            lore {
                addAll(list().map { !"&7- &f$it" })
            }
            click {
                ClickType.LEFT {
                    list.mut { add(UUID.randomUUID().toString()) }
                }
                ClickType.DROP {
                    list.mut { clear() }
                }
            }
        } slot 2
    }

    override fun run(player: Player) = gui.open(player)
}