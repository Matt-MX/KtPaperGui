package com.mattmx.ktgui.impl

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange
import com.mattmx.ktgui.event.ParentEventCallback
import com.mattmx.ktgui.event.PlayerScrollHotbarEvent
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.InventoryTypes
import net.kyori.adventure.text.Component

open class PacketGuiHotbarScreen<T : PacketGuiHotbarScreen<T>> : PacketGuiInventoryScreen<T>(
    GuiType.ofType(InventoryTypes.CUSTOM_HOTBAR),
    Component.empty()
) {
    var currentIndex: Int = 0
        set(value) {
            require(value in (0..<9)) { "currentIndex must be between 0..8" }
            field = value
        }
    open val scroll by lazy { ParentEventCallback<PlayerScrollHotbarEvent, T>(this as T) }

    open fun handleHeldItemChange(player: Any, packet: WrapperPlayClientHeldItemChange) {
        val event = PlayerScrollHotbarEvent(player, packet, this)
    }

    override fun refreshTitle(player: Any) {
        error("Cannot refresh title since a hot bar does not contain a title.")
    }

    override fun refresh(player: Any) {
        TODO("Not yet implemented")
    }

    override fun open(player: Any) = apply {
        TODO()
    } as T
}