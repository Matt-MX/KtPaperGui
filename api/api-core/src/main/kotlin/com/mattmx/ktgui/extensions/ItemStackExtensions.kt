package com.mattmx.ktgui.extensions

import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Deprecated("We should no longer use this, since we want to switch to components. It also doesn't give much control over what people want to format.")
fun ItemStack.format(player: Player?, method: (String, Player?) -> String) {
    val imeta = this.itemMeta
    imeta?.setDisplayName(method(imeta.displayName, player))
    imeta?.lore = imeta?.lore?.map { line -> method(line, player) }
    this.itemMeta = imeta
}

fun ItemStack.format(player: OfflinePlayer?, method: (Component, OfflinePlayer?) -> Component) {
    val itemMeta = this.itemMeta
    itemMeta.displayName(itemMeta.displayName()?.apply { method(this, player) })
    itemMeta.lore(itemMeta.lore()?.map { line -> method(line, player) })
    this.itemMeta = itemMeta
}