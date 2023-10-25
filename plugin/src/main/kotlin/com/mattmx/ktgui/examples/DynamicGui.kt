package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.dynamic.DynamicButton
import com.mattmx.ktgui.components.button.dynamic.Listenable
import com.mattmx.ktgui.components.button.dynamic.depends
import com.mattmx.ktgui.components.screen.GuiScreen
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import kotlin.math.max
import kotlin.math.min

class DynamicGui : GuiScreen("Dynamic", type = InventoryType.HOPPER) {
    private val percentage = Listenable(0)

    init {
        val dep = depends(percentage) {
            GuiButton(Material.ARMOR_STAND)
                .named("&c$percentage") childOf this slot 2
        }

        GuiButton(Material.ARROW)
            .named("&cChange")
            .lore {
                this += "&fRight click to remove 10%"
                this += "&fLeft click to add 10%"
            }.click {
                left = {
                    percentage.set(min(percentage.get() + 10, 100))
                }
                right = {
                    percentage.set(max(percentage.get() - 10, 0))
                }
            } slot 3 childOf this

        close {
            dep.destroy()
        }
    }
}