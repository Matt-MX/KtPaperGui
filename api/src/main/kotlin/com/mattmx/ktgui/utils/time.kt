package com.mattmx.ktgui.utils

/**
 * Amount of ticks in ticks (lol)
 */
val Int.ticks
    get() = this.toLong()

/**
 * Amount of ticks in seconds
 */
val Int.seconds
    get() = this * 20L

/**
 * Amount of ticks in seconds
 */
val Double.seconds
    get() = (this * 20).toLong()

/**
 * Amount of ticks in minutes
 */
val Int.minutes
    get() = seconds * 60L

/**
 * Amount of ticks in minutes
 */
val Double.minutes
    get() = (this * 60).toLong()