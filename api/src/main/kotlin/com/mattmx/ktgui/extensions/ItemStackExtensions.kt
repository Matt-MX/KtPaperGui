package com.mattmx.ktgui.extensions

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun ItemStack.format(player: Player?) {
    val imeta = this.itemMeta
    imeta?.setDisplayName(imeta.displayName.color(player))
    imeta?.lore = imeta?.lore?.map { line -> line.color(player) }
    this.itemMeta = imeta
}