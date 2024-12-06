package com.mattmx.ktgui

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
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
    var windowId = GuiManager.getInstance<PacketEventsGuiManager>().getWindowId()
    var stateId: Int = 0
    val click by lazy { ClickEventCallback<T, PlayerClickButtonEvent<*>>(this as T) }
    val close by lazy { ParentEventCallback<Any, T>(this as T) }

    fun handleClick(player: Any, packet: WrapperPlayClientClickWindow) {
        val button = items[packet.slot]
        val event = PlayerClickButtonEvent(player, button, packet)

        click.apply(event)

        if (event.continueEventCallback) {
            button?.handleClick(event)
        }

        if (event.cancelled) {
            // Keep clicked item as was
            val setItemPacket = WrapperPlayServerSetSlot(
                windowId,
                stateId,
                packet.slot,
                button?.buildItem() ?: ItemStack.EMPTY
            )
            PacketEvents.getAPI()
                .playerManager
                .sendPacket(player, setItemPacket)

            // Set held item to nothing todo(matt): maybe we should track their held item in manager?
            val setCursorItemPacket = WrapperPlayServerSetSlot(
                -1,
                stateId,
                packet.slot,
                ItemStack.EMPTY
            )
            PacketEvents.getAPI()
                .playerManager
                .sendPacket(player, setCursorItemPacket)
        }
    }

    fun handleClose(player: Any, packet: WrapperPlayClientCloseWindow) {
//        if (packet.windowId != windowId) {
//            return
//        }

        close.apply(player)
        unsetActiveGui(player)
    }

    fun createOpenWindowPacket(): WrapperPlayServerOpenWindow {
        return WrapperPlayServerOpenWindow(windowId, guiType.type.id, title)
    }

    fun createWindowContentsPacket(): WrapperPlayServerWindowItems {
        val slottedItems = getVisibleGuiButtons().mapValues { (i, b) -> b.buildItem() }

        val items = mutableListOf<ItemStack>()
        for (slot in (0..<guiType.getTotalSlots())) {
            items.add(slottedItems[slot] ?: ItemStack.EMPTY)
        }

        return WrapperPlayServerWindowItems(windowId, stateId, items, ItemStack.EMPTY)
    }

    override fun open(player: Any) {
        setActiveGui(player)
        val playerManager = PacketEvents.getAPI().playerManager

        playerManager.sendPacket(player, createOpenWindowPacket())
        playerManager.sendPacket(player, createWindowContentsPacket())
    }

    override fun getVisibleGuiButtons(): Map<Int, PacketGuiButton<*>> {
        val visibleRange = (0..guiType.getTotalSlots())
        return items.filterKeys { it in visibleRange }
    }
}