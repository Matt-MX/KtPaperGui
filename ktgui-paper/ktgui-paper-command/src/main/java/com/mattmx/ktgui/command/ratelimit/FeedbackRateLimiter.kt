package com.mattmx.ktgui.command.ratelimit

import com.mattmx.ktgui.event.EventCallback
import com.mojang.brigadier.builder.ArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

class FeedbackRateLimiter(
    private val duration: Duration
) : RateLimitProvider {
    val onRateLimitActive = EventCallback<RateLimitCallback>()
    private val map = hashMapOf<CommandSender, Instant>()

    override fun getRemainingDuration(player: CommandSender): Duration {
        return map[player]?.let { instant ->
            java.time.Duration.between(Instant.now(), instant).toKotlinDuration()
        } ?: Duration.ZERO
    }

    override fun markExecution(player: CommandSender) {
        map[player] = Instant.now() + duration.toJavaDuration()
    }

    fun apply(argumentBuilder: ArgumentBuilder<CommandSourceStack, *>) {
        val previous = argumentBuilder.getRequirement()
        argumentBuilder.requires { source -> previous.test(source) && canExecute(source.sender) }
    }

    class RateLimitCallback(
        val sender: CommandSender,
        val duration: Duration
    )
}

fun commandRateLimiter(
    duration: Duration,
    onRateLimit: (FeedbackRateLimiter.RateLimitCallback.() -> Unit)? = null
): FeedbackRateLimiter {
    val limiter = FeedbackRateLimiter(duration)

    if (onRateLimit != null) {
        limiter.onRateLimitActive(onRateLimit)
    }

    return limiter
}

fun ArgumentBuilder<CommandSourceStack, *>.rateLimit(
    duration: Duration,
    onRateLimit: (FeedbackRateLimiter.RateLimitCallback.() -> Unit)? = null
): FeedbackRateLimiter {
    val limiter = commandRateLimiter(duration, onRateLimit)

    limiter.apply(this)

    return limiter
}