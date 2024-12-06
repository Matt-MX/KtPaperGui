package com.mattmx.ktgui.screen

import com.mattmx.ktgui.GuiButton
import net.kyori.adventure.text.Component

abstract class GuiScreen<P, B : GuiButton<*, *, *, *>>(
    var guiType: GuiType,
    open var title: Component
) {
    var windowIdentifier: String = GuiButton.EMPTY_ID
    var items = hashMapOf<Int, B>()

    infix fun B.slot(slot: Int): B {
        items[slot] = this

        return this
    }

    infix fun B.slots(slots: List<Int>): B {
        slots.forEach { slot(it) }

        return this
    }

    fun slot(x: Int, y: Int) = Slots.ofPosition(x, y)

    abstract fun getVisibleGuiButtons() : Map<Int, B>

    abstract fun open(player: P)
}