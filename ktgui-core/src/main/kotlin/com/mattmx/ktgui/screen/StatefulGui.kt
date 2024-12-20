package com.mattmx.ktgui.screen

import java.util.*

class StatefulGui<P : GuiScreen<*, *>, E>(
    val gui: P,
    initial: E
) {
    var state = initial
        set(value) {
            field = value

            states[value]?.invoke()
        }
    private val states = Collections.synchronizedMap(hashMapOf<E, () -> Unit>())

    init {
        gui.open {
            states[state]?.invoke()
        }
    }

    fun state(state: E, callback: () -> Unit) = apply {
        this.states[state] = callback
    }
}