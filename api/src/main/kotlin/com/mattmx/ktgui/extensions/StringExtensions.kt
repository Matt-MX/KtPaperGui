package com.mattmx.ktgui.extensions

import org.bukkit.ChatColor

fun String.stripColor(): String {
    return ChatColor.stripColor(this) ?: ""
}