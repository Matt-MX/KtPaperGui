package com.mattmx.ktgui.extensions

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun ItemStack.format(player: Player?, method: (String, Player?) -> String) {
    val imeta = this.itemMeta
    imeta?.setDisplayName(method(imeta.displayName, player))
    imeta?.lore = imeta?.lore?.map { line -> method(line, player) }
    this.itemMeta = imeta
}