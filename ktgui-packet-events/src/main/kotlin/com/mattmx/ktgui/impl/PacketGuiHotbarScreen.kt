package com.mattmx.ktgui.impl

import com.mattmx.ktgui.click.ClickEventCallback
import com.mattmx.ktgui.event.PlayerClickButtonEvent
import com.mattmx.ktgui.event.PlayerScrollHotbarEvent
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.InventoryTypes
import com.mattmx.ktgui.util.ParentEventCallback
import net.kyori.adventure.text.Component

class PacketGuiHotbarScreen<T : PacketGuiHotbarScreen<T>> : GuiScreen<Any, PacketGuiButton<*>>(
    GuiType.ofType(InventoryTypes.CUSTOM_HOTBAR),
    Component.empty()
) {
    val click by lazy { ClickEventCallback<T, PlayerClickButtonEvent<*>>(this as T) }
    val scroll by lazy { ParentEventCallback<PlayerScrollHotbarEvent, T>(this as T) }
    override val close by lazy { ParentEventCallback<Any, T>(this as T) }
    override val open by lazy { ParentEventCallback<Any, T>(this as T) }

    override fun refreshTitle(player: Any) {
        // Title is not possible with hotbar
    }

    override fun refresh(player: Any) {
        TODO("Not yet implemented")
    }

    override fun open(player: Any) = apply {
        TODO()
    } as T
}