package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.Material
import java.util.*

class BigButtonWidget(
    var size: Int = 3,
    material: Material = Material.STONE
) : GuiButton(material) {

    override infix fun slot(slot: Int) : GuiButton {
        /**
         * Let's make sure to register our other buttons.
         */
        val slots = arrayListOf(slot)
        repeat(size * size - 1) {
            /**
             * (initial slot) + offset + (new row(s))?
             */
            slots.add(slot + it + (it % slot))
        }
        return super.slots()
    }

    override fun copy(parent: IGuiScreen): GuiButton {
        val i = BigButtonWidget(size)
        i.item = item
        i.parent = parent
        return i
    }

}