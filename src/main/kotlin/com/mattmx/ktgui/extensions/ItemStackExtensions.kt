package com.mattmx.ktgui.extensions

import com.mattmx.ktgui.utils.Chat
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun ItemStack.format(player: Player?) {
        val imeta = this.itemMeta
        imeta?.setDisplayName(Chat.format(imeta.displayName, player))
        imeta?.lore = imeta?.lore?.map { line -> Chat.format(line, player) }
        this.itemMeta = imeta
}