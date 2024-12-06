package com.mattmx.ktgui

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import com.mattmx.ktgui.click.ClickEventCallback
import com.mattmx.ktgui.event.PlayerClickButtonEvent
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.util.ParentEventCallback
import net.kyori.adventure.text.Component

@Suppress("UNCHECKED_CAST")
open class PacketGuiScreen<T : PacketGuiScreen<T>>(
    guiType: GuiType,
    title: Component
) : GuiScreen<Any, PacketGuiButton<*>>(guiType, title) {
    var windowId = PacketEventsGuiManager.instance.getWindowId()
    val click by lazy { ClickEventCallback<T, PlayerClickButtonEvent<*>>(this as T) }
    val close by lazy { ParentEventCallback<Any, T>(this as T) }

    fun handleClick(player: Any, packet: WrapperPlayClientClickWindow) {
        val button = items[packet.slot]
        val event = PlayerClickButtonEvent(player, button, packet)

        click.apply(event)

        if (event.cancelled) {
            // Keep clicked item as was
            val setItemPacket = WrapperPlayServerSetSlot(
                windowId,
                0,
                packet.slot,
                button?.buildItem() ?: ItemStack.EMPTY
            )
            PacketEvents.getAPI()
                .playerManager
                .sendPacket(player, setItemPacket)

            // Set held item to nothing todo(matt): maybe we should track their held item in manager?
            PacketEvents.getAPI()
                .playerManager
                .sendPacket(player, WrapperPlayServerSetCursorItem(ItemStack.EMPTY))
        }

        if (event.continueEventCallback) {
            button?.handleClick(event)
        }
    }

    fun handleClose(player: Any, packet: WrapperPlayClientCloseWindow) {
        if (packet.windowId != windowId) {
            return
        }

        close.apply(player)
    }

    fun createOpenWindowPacket(): WrapperPlayServerOpenWindow {
        return WrapperPlayServerOpenWindow(windowId, 0, title)
    }

    fun createWindowContentsPacket(): WrapperPlayServerWindowItems {
        val slottedItems = getVisibleGuiButtons().mapValues { (i, b) -> b.buildItem() }

        val items = mutableListOf<ItemStack>()
        for (slot in (0..<guiType.getTotalSlots())) {
            items.add(slottedItems[slot] ?: ItemStack.EMPTY)
        }

        return WrapperPlayServerWindowItems(windowId, 0, items, null)
    }

    override fun open(player: Any) {
        val playerManager = PacketEvents.getAPI().playerManager

        playerManager.sendPacket(player, createOpenWindowPacket())
        playerManager.sendPacket(player, createWindowContentsPacket())
    }

    override fun getVisibleGuiButtons(): Map<Int, PacketGuiButton<*>> {
        TODO("Not yet implemented")
    }
}