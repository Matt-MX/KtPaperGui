package com.mattmx.ktgui.impl

import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.inventory.ItemStack
import com.github.retrooper.packetevents.protocol.item.ItemStack as PacketEventsItemStack

class BukkitConvertedButton<T : BukkitConvertedButton<T>>(
    val bukkit: ItemStack,
    private val packetEvents: PacketEventsItemStack = SpigotConversionUtil.fromBukkitItemStack(bukkit)
) : PacketGuiButton<T>(packetEvents) {

    init {
        amount = packetEvents.amount

        components = packetEvents.components
            .patches
            .mapNotNull { (k, v) -> v.orElse(null)?.let { k to it } }
            .toMap(mutableMapOf())

        postBuild {
            nbt = packetEvents.nbt?.copy()
        }
    }

}