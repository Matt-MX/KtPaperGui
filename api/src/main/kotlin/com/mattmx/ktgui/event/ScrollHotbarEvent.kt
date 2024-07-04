package com.mattmx.ktgui.event

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemHeldEvent

class ScrollHotbarEvent(
    val player: Player,
    val difference: Int,
    val event: PlayerItemHeldEvent
)