package com.mattmx.ktgui.event

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.mattmx.ktgui.PacketGuiButton
import com.mattmx.ktgui.click.ClickButtonEvent
import com.mattmx.ktgui.click.ClickTypes

class PlayerClickButtonEvent<T : PacketGuiButton<T>>(
    private val player: Any,
    private val clickedButton: PacketGuiButton<T>?,
    val packet: WrapperPlayClientClickWindow
) : ClickButtonEvent {
    private val clickType = ClickTypes.match(
        packet.windowClickType.ordinal,
        packet.button,
        packet.actionNumber.orElse(0)
    )
    var cancelled = true
    var continueEventCallback: Boolean = true
    val button: PacketGuiButton<T>
        get() = clickedButton!!

    fun isEmpty() = clickedButton == null

    override fun shouldContinueEventCallback() = continueEventCallback

    override fun isCancelled() = cancelled

    override fun getClickType() = clickType

    override fun <T> getPlayer() = player as T
}