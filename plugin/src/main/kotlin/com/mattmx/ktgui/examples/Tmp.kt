package com.mattmx.ktgui.examples

import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import kotlin.math.floor

class Tmp {
    val instance = gui(!"Loading", InventoryType.HOPPER) {

        var lastClicked = System.currentTimeMillis()
        val canClick = { System.currentTimeMillis() - lastClicked > 1000 }

        click.any {
            if (!canClick()) {
                shouldContinueCallback(false)
                player.sendMessage(!"&cSlow down!")
                return@any
            }

            lastClicked = System.currentTimeMillis()
        }

        button(Material.DIAMOND) {
            click.left {
                player.giveExp(1)
            }
        } slot floor(last() / 2.0).toInt()

    }
}