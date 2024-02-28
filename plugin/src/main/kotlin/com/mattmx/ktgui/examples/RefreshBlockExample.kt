package com.mattmx.ktgui.examples

import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.dsl.refresh
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.pretty
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import java.text.DateFormat
import java.time.Duration
import java.util.*

class RefreshBlockExample : Example {
    val gui = gui(!"0s", InventoryType.HOPPER) {
        val timeOpened = System.currentTimeMillis()

        val block = refresh(20) {
            val timeElapsed = Duration.ofMillis(System.currentTimeMillis() - timeOpened)
            button(Material.CLOCK) {
                named(!"&fOpen for &7${timeElapsed.pretty()}")
                lore {
                    add(Component.empty())
                    val date = Date()
                    val format = DateFormat.getTimeInstance()
                    add(!"&a${format.format(date)}")
                }
            } slot 2
            title = !timeElapsed.pretty()
        }

        button(Material.REDSTONE) {
            named(!"&aManually refresh")

            click.left {
                block.refresh()
            }

        } slot last()
    }

    override fun run(player: Player) {
        gui.open(player)
    }
}