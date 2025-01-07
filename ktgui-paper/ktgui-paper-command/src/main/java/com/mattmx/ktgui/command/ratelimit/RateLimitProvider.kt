package com.mattmx.ktgui.command.ratelimit

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

interface RateLimitProvider {

    fun getRemainingDuration(player: CommandSender) : Duration

    fun canExecute(player: CommandSender) = getRemainingDuration(player)
        .let { dur -> dur.isNegative() || dur == Duration.ZERO }

    fun markExecution(player: CommandSender)

    companion object {

        fun duration(duration: Duration): RateLimitProvider {
            val map = hashMapOf<CommandSender, Instant>()

            return object : RateLimitProvider {
                override fun getRemainingDuration(player: CommandSender): Duration {
                    return map[player]?.let { instant ->
                        java.time.Duration.between(Instant.now(), instant).toKotlinDuration()
                    } ?: Duration.ZERO
                }

                override fun markExecution(player: CommandSender) {
                    map[player] = Instant.now() + duration.toJavaDuration()
                }
            }
        }

    }

}