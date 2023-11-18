package com.mattmx.ktgui.components.signal

import com.mattmx.ktgui.components.screen.GuiScreen

/**
 * Easier method for creating a [Signal].
 *
 * @param value initial value for the [Signal]
 * @return the [Signal] object
 */
fun <T, V> GuiScreen.signal(value: V) =
    Signal<T, V>(value, this)