package com.mattmx.ktgui.extensions

import com.mattmx.ktgui.item.itemBuilder
import com.mattmx.ktgui.utils.component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Deprecated("We should no longer use this, since we want to switch to components. It also doesn't give much control over what people want to format.")
fun ItemStack.format(player: Player?, method: (String, Player?) -> String) {
    val imeta = this.itemMeta
    imeta?.setDisplayName(method(imeta.displayName, player))
    imeta?.lore = imeta?.lore?.map { line -> method(line, player) }
    this.itemMeta = imeta
}

fun main() {
    itemBuilder(Material.STONE) {
        name = "&c&lFormatted name"
        // todo needs to apply formatting via [component()]
    }.build()
}