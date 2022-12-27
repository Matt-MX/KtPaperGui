package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.lang.Integer.max
import java.lang.Integer.min

class NumberWidgetButton(
    material: Material = Material.STONE,
    val startValue: Int = 1,
    val step: Int = 1
) : GuiButton(material) {
    init {
        amount(startValue)
    }

    override fun thisClicked(e: ButtonClickedEvent) {
        when (e.event.click) {
            ClickType.RIGHT -> {
                amount(min(64, max(1, item!!.amount + step)))
                update(e.player)
            }
            ClickType.LEFT -> {
                amount(min(64, max(1, item!!.amount - step)))
                update(e.player)
            }
            else -> {}
        }
    }

    override fun copy(parent: IGuiScreen): NumberWidgetButton {
        val n = NumberWidgetButton(startValue = this.startValue, step = this.step)
        n.item = item
        n.amount(this.startValue)
        n.parent = parent
        return n
    }
}