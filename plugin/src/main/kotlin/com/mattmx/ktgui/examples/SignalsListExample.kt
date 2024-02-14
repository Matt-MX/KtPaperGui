package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.signal.signal
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.effect
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.scheduling.not
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryType
import java.util.*

/**
 * Unfortunately, we cannot "listen" for something inside a variable being reassigned.
 * This could be something like adding to a list, so we must instead assign the signal
 * to a variable.
 */
class SignalsListExample : Example {
    val gui = gui(!"Signals (List)", InventoryType.HOPPER) {
        // Instead of using 'by' keyword we just assign the signal
        val list = signal(arrayListOf<String>())

        effect {
            button(Material.KNOWLEDGE_BOOK) {
                // To access the variable we invoke it.
                named(!"&7This list has &f${list().size} items:")
                lore {
                    addAll(list().map { !"&7- &f$it" })
                }
                click {
                    ClickType.LEFT {
                        // Then to modify it internally we can use a variety of methods
                        list.mut { add(UUID.randomUUID().toString()) }
                    }
                    ClickType.DROP {
                        list.mut { clear() }
                    }
                }
            } slot 2
        }
    }

    override fun run(player: Player) = gui.open(player)
}