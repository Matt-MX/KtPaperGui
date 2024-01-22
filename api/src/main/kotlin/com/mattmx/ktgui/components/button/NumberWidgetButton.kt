package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import java.lang.Integer.max
import java.lang.Integer.min

class NumberWidgetButton(
    material: Material = Material.STONE,
    val startValue: Int = 1,
    val step: Int = 1
) : GuiButton<NumberWidgetButton>(material) {
    init {
        amount(startValue)
    }

    override fun onButtonClick(e: ButtonClickedEvent<*>) {
        when (e.event.click) {
            ClickType.RIGHT -> {
                amount(min(64, max(1, item!!.amount + step)))
                update(e.player)
            }
            ClickType.LEFT -> {
                amount(min(64, max(1, item!!.amount - step)))
                update(e.player)
            }
            else -> { click.run(e) }
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