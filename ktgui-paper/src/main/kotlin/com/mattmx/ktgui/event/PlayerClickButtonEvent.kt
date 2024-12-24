package com.mattmx.ktgui.event

import com.mattmx.ktgui.impl.PaperGuiButton
import com.mattmx.ktgui.click.ClickButtonEvent
import com.mattmx.ktgui.click.ClickTypes
import org.bukkit.event.inventory.InventoryClickEvent

class PlayerClickButtonEvent<T : PaperGuiButton<T>>(
    private val player: Any,
    private val clickedButton: PaperGuiButton<T>?,
    val event: InventoryClickEvent
) : ClickButtonEvent {
    private val clickType = ClickTypes.LEFT // TODO
    var cancelled = true
    var continueEventCallback: Boolean = true
    val button: PaperGuiButton<T>
        get() = clickedButton!!

    fun isEmpty() = clickedButton == null

    override fun shouldContinueEventCallback() = continueEventCallback

    override fun isCancelled() = cancelled

    override fun getClickType() = clickType

    override fun <T> getPlayer() = player as T
}