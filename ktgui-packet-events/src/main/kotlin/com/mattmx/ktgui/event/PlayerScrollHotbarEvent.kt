package com.mattmx.ktgui.event

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange
import com.mattmx.ktgui.impl.PacketGuiHotbarScreen
import kotlin.math.max

class PlayerScrollHotbarEvent(
    val player: Any,
    val packet: WrapperPlayClientHeldItemChange,
    val gui: PacketGuiHotbarScreen<*>
) {
    var cancelled = false
    val scrollAmount = max(1, packet.slot - gui.currentIndex)
}