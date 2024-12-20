package com.mattmx.ktgui

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import java.util.*

open class InventoryTracker : PacketListenerAbstract() {
    private val inventory = Collections.synchronizedMap(hashMapOf<Any, TrackedInventory>())

    override fun onPacketSend(event: PacketSendEvent) {
        val player = event.getPlayer<Any>()

        when (event.packetType) {
            PacketType.Play.Server.SET_SLOT -> {
                val packet = WrapperPlayServerSetSlot(event)
                if (packet.windowId != 0) return
                setSlot(player, packet.stateId, packet.slot, packet.item)
            }

            PacketType.Play.Server.SET_PLAYER_INVENTORY -> {
                val packet = WrapperPlayServerSetPlayerInventory(event)

                setSlot(player, null, packet.slot, packet.stack)
            }

            PacketType.Play.Server.WINDOW_ITEMS -> {
                val packet = WrapperPlayServerWindowItems(event)
                if (packet.windowId != 0) return

                val cached = inventory.getOrPut(player) {
                    TrackedInventory(getDefaultInventory(player), packet.stateId)
                }

                cached.stateId = packet.stateId

                for ((slot, stack) in packet.items.withIndex()) {

                    if (slot >= cached.contents.size) return

                    cached.contents[slot] = stack
                }
            }
        }
    }

    fun resetPlayerSlot(player: Any, slot: Int) {
        val inventory = inventory[player] ?: return

        val packet =
            WrapperPlayServerSetSlot(0, inventory.stateId, slot, inventory.contents.getOrElse(slot) { ItemStack.EMPTY })

        PacketEvents.getAPI()
            .playerManager
            .sendPacket(player, packet)
    }

    fun resetPlayerInventory(player: Any) {
        val inventory = inventory[player] ?: return

        for ((index, item) in inventory.contents.withIndex()) {
            val packet = WrapperPlayServerSetSlot(0, inventory.stateId, index, item ?: ItemStack.EMPTY)

            PacketEvents.getAPI()
                .playerManager
                .sendPacket(player, packet)
        }
    }

    fun remove(player: Any) = inventory.remove(player)

    open fun getDefaultInventory(player: Any): Array<ItemStack?> {
        return arrayOfNulls(getDefaultInventorySize(player))
    }

    open fun setSlot(player: Any, stateId: Int?, slot: Int, stack: ItemStack?) {
        val cached = inventory.getOrPut(player) {
            TrackedInventory(getDefaultInventory(player), stateId ?: 0)
        }

        if (stateId != null) {
            cached.stateId = stateId
        }

        if (slot >= cached.contents.size) return

        cached.contents[slot] = stack
    }

    open fun getDefaultInventorySize(player: Any): Int {
        return 54
    }

    class TrackedInventory(
        var contents: Array<ItemStack?>,
        var stateId: Int
    )
}