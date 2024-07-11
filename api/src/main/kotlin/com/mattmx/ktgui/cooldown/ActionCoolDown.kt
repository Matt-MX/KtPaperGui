package com.mattmx.ktgui.cooldown

import java.time.Duration
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class ActionCoolDown<T : Any>(
    duration: Duration
) : CoolDown<T> {
    var shouldRefreshIfAlreadyOnCoolDown = false
    private val expire = duration.toMillis()
    private val cache = Collections.synchronizedMap(hashMapOf<Any, Long>())

    init {
        register(this)
    }

    override fun removeAnyUsers(vararg user: Any) = user.forEach { cache.remove(it) }

    override fun removeUser(user: Any) {
        cache.remove(user)
    }

    override fun test(user: T): Boolean {
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

    override fun millisRemaining(user: T): Long {
        return max(-1L, expire - (System.currentTimeMillis() - cache.getOrDefault(user, 0)))
    }

    fun test(user: T, block: (Duration) -> Unit): Unit? {
        val timeLeft = millisRemaining(user)
        val valid = timeLeft < 0

        if (valid || shouldRefreshIfAlreadyOnCoolDown)
            cache[user] = System.currentTimeMillis()

        if (valid) {
            block(Duration.ofMillis(timeLeft))
            return Unit
        }
        return null
    }

    companion object {
        private val registeredCoolDowns = Collections.synchronizedList<ActionCoolDown<*>>(arrayListOf())

        fun register(coolDown: ActionCoolDown<*>) {
            registeredCoolDowns.add(coolDown)
        }

        fun unregister(coolDown: ActionCoolDown<*>) {
            registeredCoolDowns.remove(coolDown)
        }

        fun removeUsers(vararg user: Any) {
            registeredCoolDowns.forEach { it.removeAnyUsers(*user) }
        }
    }

}