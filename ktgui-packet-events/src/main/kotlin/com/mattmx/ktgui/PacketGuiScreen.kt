package com.mattmx.ktgui

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.wrapper.PacketWrapper
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
import java.util.Optional

@Suppress("UNCHECKED_CAST")
open class PacketGuiScreen<T : PacketGuiScreen<T>>(
    guiType: GuiType,
    title: Component
) : GuiScreen<Any, PacketGuiButton<*>>(guiType, title) {
    var windowId = GuiManager.getInstance<PacketEventsGuiManager>().getWindowId()
    var stateId: Int = 0
    val click by lazy { ClickEventCallback<T, PlayerClickButtonEvent<*>>(this as T) }
    override val close by lazy { ParentEventCallback<Any, T>(this as T) }
    override val open by lazy { ParentEventCallback<Any, T>(this as T) }
    var visiblePagesOverride: Optional<() -> IntRange> = Optional.empty()

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

        unsetActiveGui(player)
        close.apply(player)
    }

    fun createOpenWindowPacket(): WrapperPlayServerOpenWindow {
        return WrapperPlayServerOpenWindow(windowId, guiType.type.id, title)
    }

    fun createWindowContentsPacket(): WrapperPlayServerWindowItems {
        val visibleSlots = getVisibleGuiButtons()

        val contents = mutableListOf<ItemStack>()
        for (i in (0..<guiType.getTotalSlots())) {
            val offsetSlot = visibleSlots.first + i
            val itemStack = items[offsetSlot]?.buildItem() ?: ItemStack.EMPTY
            contents.add(itemStack)
        }

        return WrapperPlayServerWindowItems(windowId, stateId, contents, ItemStack.EMPTY)
    }

    override fun open(player: Any) = apply {
        open.apply(player)
        setActiveGui(player)
        val playerManager = PacketEvents.getAPI().playerManager

        playerManager.sendPacket(player, createOpenWindowPacket())
        playerManager.sendPacket(player, createWindowContentsPacket())
    } as T

    override fun getVisibleGuiButtons(): IntRange {
        return visiblePagesOverride.orElse {
            (0..guiType.getTotalSlots())
        }.invoke()
    }

    override fun refresh(player: Any) {
        val contents = createWindowContentsPacket()

        PacketEvents.getAPI()
            .playerManager
            .sendPacket(player, contents)
    }

    override fun refreshTitle(player: Any) {
        val updateTitle = createOpenWindowPacket()

        PacketEvents.getAPI()
            .playerManager
            .sendPacket(player, updateTitle)
    }

    /**
     * Attempts to update listener's GUIs if any slots are
     * updated in real time.
     *
     * @param state should we update automatically?
     * @return self
     */
    fun updateOnModify(state: Boolean) = apply {
        if (!state) {
            slotUpdated.clear()
        } else {
            slotUpdated {
                val packet = WrapperPlayServerSetSlot(
                    windowId,
                    stateId,
                    slot,
                    new?.buildItem() ?: ItemStack.EMPTY
                )
                sendPacketsToViewers(packet)
            }
        }
    } as T

    fun sendPacketsToViewers(vararg packets: PacketWrapper<*>) {
        if (packets.isEmpty()) return

        val playerManager = PacketEvents.getAPI().playerManager
        for ((player, _) in GuiManager.getInstance().getActiveOfInstance(this)) {
            for (packet in packets) {
                playerManager.sendPacket(player, packet)
            }
        }
    }
}