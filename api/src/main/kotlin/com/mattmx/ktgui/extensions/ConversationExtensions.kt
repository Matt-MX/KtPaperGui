package com.mattmx.ktgui.extensions

import org.bukkit.conversations.ConversationContext
import org.bukkit.entity.Player

fun ConversationContext.getPlayer() : Player {
    return this.forWhom as Player
}