package com.mattmx.ktgui.cooldown

import java.time.Duration
import java.util.*
import kotlin.math.max
import kotlin.math.min

class ActionCoolDown<T>(
    duration: Duration
) {
    var shouldRefreshIfAlreadyOnCoolDown = false
    private val expire = duration.toMillis()
    private val cache = Collections.synchronizedMap(hashMapOf<Any, Long>())

    init {
        register(this)
    }

    fun removeAnyUsers(vararg user: Any) = user.forEach { cache.remove(it) }

    fun removeUser(user: T) = cache.remove(user)

    fun test(user: T): Boolean {
        val currentMillis = System.currentTimeMillis()
        val returnValue = if (!cache.containsKey(user)) {
            true
        } else {
            currentMillis - cache.getOrDefault(user, 0) >= expire
        }

        if (returnValue || shouldRefreshIfAlreadyOnCoolDown)
            cache[user] = currentMillis

        return returnValue
    }

    fun timeRemaining(user: T): Long {
        return max(-1L, expire - (System.currentTimeMillis() - cache.getOrDefault(user, 0)))
    }

    fun test(user: T, block: (Duration) -> Unit): Unit? {
        val timeLeft = timeRemaining(user)
        val valid = timeLeft < 0

        if (valid || shouldRefreshIfAlreadyOnCoolDown)
            cache[user] = System.currentTimeMillis()

        if (valid) {
            block(Duration.ofMillis(timeLeft))
        }
        return null
    }

    companion object {
        private val registeredCoolDowns = Collections.synchronizedList<ActionCoolDown<*>>(arrayListOf())

        fun register(coolDown: ActionCoolDown<*>) {
            registeredCoolDowns.add(coolDown)
        }

        fun removeUsers(vararg user: Any) {
            registeredCoolDowns.forEach { it.removeAnyUsers(*user) }
        }
    }

}