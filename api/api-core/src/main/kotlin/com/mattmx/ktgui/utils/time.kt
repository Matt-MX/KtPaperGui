package com.mattmx.ktgui.utils

val Int.ticks
    get() = this.toLong()

val Int.seconds
    get() = ticks * 20L

val Double.seconds
    get() = (this * 20).toLong()

val Int.minutes
    get() = seconds * 60L

val Double.minutes
    get() = (this * 20 * 60).toLong()