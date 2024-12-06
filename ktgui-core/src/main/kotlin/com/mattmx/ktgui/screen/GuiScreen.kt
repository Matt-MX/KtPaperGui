package com.mattmx.ktgui.screen

import com.mattmx.ktgui.GuiButton
import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.util.ParentEventCallback
import net.kyori.adventure.text.Component

abstract class GuiScreen<P : Any, B : GuiButton<*, *, *, *>>(
    var guiType: GuiType,
    open var title: Component
) {
    var windowIdentifier: String = GuiButton.EMPTY_ID
    var items = hashMapOf<Int, B>()
    abstract val open: ParentEventCallback<P, *>
    abstract val close: ParentEventCallback<P, *>

    infix fun B.slot(slot: Int): B {
        items[slot] = this

        return this
    }

    infix fun B.slots(slots: List<Int>): B {
        slots.forEach { slot(it) }

        return this
    }

    operator fun set(slot: Int, button: B) = button.slot(slot)
    operator fun set(slot: Int, button: Any) = (button as? B)?.slot(slot)

    fun slot(x: Int, y: Int) = Slots.ofPosition(x, y)

    fun setActiveGui(player: P) {
        GuiManager.getInstance().setActiveGui(player, this)
    }

    fun unsetActiveGui(player: P) {
        GuiManager.getInstance().removeActiveGui(player)
    }

    abstract fun getVisibleGuiButtons() : Map<Int, B>

    abstract fun open(player: P)

    abstract fun refresh(player: P)

    fun openAsAny(player: Any) = (player as? P)?.let { open(it) }
    fun refreshAsAny(player: Any) = (player as? P)?.let { refresh(it) }

    fun refresh() {
        for ((player, _) in GuiManager.getInstance().getActiveOfInstance(this)) {
            refreshAsAny(player)
        }
    }
}