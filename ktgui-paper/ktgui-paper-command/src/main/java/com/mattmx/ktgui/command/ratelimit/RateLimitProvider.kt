package com.mattmx.ktgui.command.ratelimit

import org.bukkit.entity.Player
import kotlin.time.Duration

fun interface RateLimitProvider {

    fun getRemainingDuration(player: Player) : Duration

    fun canExecute(player: Player) = getRemainingDuration(player).isNegative()

}