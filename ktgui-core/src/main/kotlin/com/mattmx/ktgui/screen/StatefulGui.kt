package com.mattmx.ktgui.screen

import com.mattmx.ktgui.GuiButton
import com.mattmx.ktgui.event.SlotUpdatedEvent
import java.util.*

class StatefulGui<P : GuiScreen<*, *>, E>(
    val gui: P,
    initial: E
) {
    private var changes = mutableMapOf<Int, Optional<GuiButton<*, *, *, *>>>()
    private var isChanging = false
    var state = initial
        set(value) {
            field = value

            synchronized(this) {
                if (isChanging) return@synchronized

                // Revert changes
                for ((slot, button) in changes.entries) {
                    button.ifPresentOrElse({ button ->
                        gui[slot] = button
                    }, {
                        gui.remove(slot)
                    })
                }
                changes.clear()

                isChanging = true
                states[value]?.invoke()
                isChanging = false
            }
        }
    private val states = Collections.synchronizedMap(hashMapOf<E, () -> Unit>())

    init {
        gui.open {
            states[state]?.invoke()
        }

        gui.slotUpdated {
            if (isChanging && !changes.containsKey(slot)) {
                changes[slot] = Optional.ofNullable(old)
            }
        }
    }

    fun state(state: E, callback: () -> Unit) = apply {
        this.states[state] = callback
    }
}