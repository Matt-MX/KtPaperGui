package com.mattmx.ktgui.cooldown

import java.time.Duration

interface CoolDown<T : Any> {

    fun test(user: T) : Boolean

    fun removeAnyUsers(vararg user: Any) = user.forEach { removeUser(it) }

    fun removeUser(user: Any)

    fun millisRemaining(user: T) : Long

    fun durationRemaining(user: T) = Duration.ofMillis(millisRemaining(user))

}