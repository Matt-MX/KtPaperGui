package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.signal.Signal
import com.mattmx.ktgui.components.signal.signal
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.effect
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryType

/**
 * Here is an example demonstrating usage of [Signal]s.
 * We can use the [signal] method and provide an initial value.
 */
class SignalsExample : Example {
    val gui = gui(!"Signals", InventoryType.HOPPER) {
        // static list of data as example
        val list = listOf("first item", "second item", "third item", "fourth item", "fifth item", "sixth item")
        // declare our signal with an initial value of 0
        var listIndex by signal(0)

        // we must use this method instead of the regular button() {} method.
        effect {
            button(Material.KNOWLEDGE_BOOK) {
                title = !"Signals &7(item $listIndex)"
                // whenever we use the signal variable, ktgui will recognize the usage and automatically
                // update your button whenever the variable changes.
                named(!"&7'&f${list[listIndex]}&7'")
                lore {
                    add(!"&7Char length: &f${list[listIndex].length}")
                }
            } slot 2
        }

        button(Material.LIME_DYE) {
            named(!"&a&l[CLICK]")
            lore {
                add(!"&7To change to the next thing!")
            }
            click {
                ClickType.LEFT {
                    // increment or wrap around and set our signal to 0
                    val old = listIndex
                    if (listIndex + 1 >= list.size)
                        listIndex = 0
                    else listIndex++
                    player.sendMessage(!"&7Next item: &f$old &7-> &f$listIndex")
                }
            }
        } slot last()
    }

    override fun run(player: Player) = gui.open(player)
}