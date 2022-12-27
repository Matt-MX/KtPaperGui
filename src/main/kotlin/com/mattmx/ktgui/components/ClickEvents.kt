package com.mattmx.ktgui.components

import com.mattmx.ktgui.components.button.ButtonClickedEvent
import org.bukkit.event.inventory.ClickType

class ClickEvents {
    var left : (ButtonClickedEvent.() -> Unit)? = null
    var shiftLeft : (ButtonClickedEvent.() -> Unit)? = null
    var right : (ButtonClickedEvent.() -> Unit)? = null
    var shiftRight : (ButtonClickedEvent.() -> Unit)? = null
    var windowBorderLeft : (ButtonClickedEvent.() -> Unit)? = null
    var windowBorderRight : (ButtonClickedEvent.() -> Unit)? = null
    var middle : (ButtonClickedEvent.() -> Unit)? = null
    var numberKey : (ButtonClickedEvent.() -> Unit)? = null
    var doubleClick : (ButtonClickedEvent.() -> Unit)? = null
    var drop : (ButtonClickedEvent.() -> Unit)? = null
    var ctrlDrop : (ButtonClickedEvent.() -> Unit)? = null
    var creative : (ButtonClickedEvent.() -> Unit)? = null
    var swapOffhand : (ButtonClickedEvent.() -> Unit)? = null
    var generic : (ButtonClickedEvent.() -> Unit)? = null

    fun accept(e: ButtonClickedEvent) {
        when (e.event.click) {
            ClickType.LEFT -> left?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.SHIFT_LEFT -> shiftLeft?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.RIGHT -> right?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.SHIFT_RIGHT -> shiftRight?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.WINDOW_BORDER_LEFT -> windowBorderLeft?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.WINDOW_BORDER_RIGHT -> windowBorderRight?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.MIDDLE -> middle?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.NUMBER_KEY -> numberKey?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.DOUBLE_CLICK -> doubleClick?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.DROP -> drop?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.CONTROL_DROP -> ctrlDrop?.also { it.invoke(e) } ?: generic?.invoke(e)
            ClickType.CREATIVE -> creative?.also { it.invoke(e) } ?: generic?.invoke(e)
            else -> generic?.invoke(e)
        }
    }

    fun copy() : ClickEvents {
        val copy = ClickEvents()
        copy.left = left
        copy.right = right
        copy.shiftRight = shiftRight
        copy.shiftLeft = shiftLeft
        copy.windowBorderLeft = windowBorderLeft
        copy.windowBorderRight = windowBorderRight
        copy.middle = middle
        copy.numberKey = numberKey
        copy.doubleClick = doubleClick
        copy.drop = drop
        copy.ctrlDrop = ctrlDrop
        copy.creative = creative
        copy.generic = generic
        return copy
    }
}