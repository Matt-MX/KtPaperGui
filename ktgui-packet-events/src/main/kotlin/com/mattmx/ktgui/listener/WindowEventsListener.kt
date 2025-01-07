package com.mattmx.ktgui.listener

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindowButton
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging
import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.impl.PacketEventsGuiManager
import com.mattmx.ktgui.impl.PacketGuiHotbarScreen

class WindowEventsListener : PacketListenerAbstract() {

    override fun onPacketReceive(event: PacketReceiveEvent) {
        when (event.packetType) {
            PacketType.Play.Client.CLICK_WINDOW -> {
                val packet = WrapperPlayClientClickWindow(event)
                val gui = GuiManager.getInstance<PacketEventsGuiManager>()
                    .getActiveGui(event.getPlayer())
                    ?: return
                gui.handleClick(event.getPlayer(), packet)
                event.isCancelled = true
            }

            PacketType.Play.Client.PLAYER_DIGGING -> {
                val packet = WrapperPlayClientPlayerDigging(event)
            }

            PacketType.Play.Client.CLOSE_WINDOW -> {
                val packet = WrapperPlayClientCloseWindow(event)
                val gui = GuiManager.getInstance<PacketEventsGuiManager>()
                    .getActiveGui(event.getPlayer())
                    ?: return
                gui.handleClose(event.getPlayer(), packet)
                event.isCancelled = true
            }

            PacketType.Play.Client.CLICK_WINDOW_BUTTON -> {
                val packet = WrapperPlayClientClickWindowButton(event)
            }

            PacketType.Play.Client.HELD_ITEM_CHANGE -> {
                val packet = WrapperPlayClientHeldItemChange(event)
                val gui = GuiManager.getInstance<PacketEventsGuiManager>()
                    .getActiveGui(event.getPlayer())
                    ?: return

                if (gui is PacketGuiHotbarScreen<*>) {
                    gui.handleHeldItemChange(event.getPlayer(), packet)
                }
            }
        }
    }

}