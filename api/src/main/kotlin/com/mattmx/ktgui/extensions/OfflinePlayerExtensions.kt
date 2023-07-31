package com.mattmx.ktgui.extensions

import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

fun OfflinePlayer.getHead(): ItemStack {
    val head = ItemStack(Material.PLAYER_HEAD)
    val meta = head.itemMeta as SkullMeta
    meta.owningPlayer = this
    head.itemMeta = meta
    return head
}