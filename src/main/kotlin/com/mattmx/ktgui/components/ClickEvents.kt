package com.mattmx.ktgui.components

import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class ClickEvents {
    var left : ((InventoryClickEvent) -> Unit)? = null
    var shiftLeft : ((InventoryClickEvent) -> Unit)? = null
    var right : ((InventoryClickEvent) -> Unit)? = null
    var shiftRight : ((InventoryClickEvent) -> Unit)? = null
    var windowBorderLeft : ((InventoryClickEvent) -> Unit)? = null
    var windowBorderRight : ((InventoryClickEvent) -> Unit)? = null
    var middle : ((InventoryClickEvent) -> Unit)? = null
    var numberKey : ((InventoryClickEvent) -> Unit)? = null
    var doubleClick : ((InventoryClickEvent) -> Unit)? = null
    var drop : ((InventoryClickEvent) -> Unit)? = null
    var ctrlDrop : ((InventoryClickEvent) -> Unit)? = null
    var creative : ((InventoryClickEvent) -> Unit)? = null
    var swapOffhand : ((InventoryClickEvent) -> Unit)? = null
    var generic : ((InventoryClickEvent) -> Unit)? = null

    fun accept(e: InventoryClickEvent) {
        when (e.click) {
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
}