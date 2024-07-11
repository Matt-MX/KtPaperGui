package com.mattmx.ktgui.cooldown

import java.time.Duration

open class PersistentCoolDown<T : Any>(
    duration: Duration,
    val impl: Impl
) : CoolDown<T> {
    var shouldRefreshIfAlreadyOnCoolDown = false
    private val expire = duration.toMillis()

    override fun test(user: T): Boolean {
        val currentMillis = System.currentTimeMillis()
        val returnValue = impl.get(user)?.let { currentMillis - it >= expire } ?: true

        if (returnValue || shouldRefreshIfAlreadyOnCoolDown) {
            impl.set(user, currentMillis)
        }

        return returnValue
    }

    override fun removeUser(user: Any) =impl.set(user, null)
    override fun millisRemaining(user: T) = impl.get(user) ?: 0L

    interface Impl {
        fun set(user: Any, lastExecuted: Long?)
        fun get(user: Any) : Long?
    }
}