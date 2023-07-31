package com.mattmx.ktgui.extensions

import org.bukkit.ChatColor
import org.bukkit.entity.Player

fun String.stripColor() : String {
    return ChatColor.stripColor(this) ?: ""
}

fun String.color(player: Player? = null, vararg placeholders: Pair<String, String>) : String {
    return com.mattmx.ktgui.utils.color(this, player, *placeholders)
}
