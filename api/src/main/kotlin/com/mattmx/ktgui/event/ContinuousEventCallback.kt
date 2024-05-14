package com.mattmx.ktgui.event

import org.bukkit.event.Cancellable

class ContinuousEventCallback<T : Cancellable> : EventCallback<T>() {

    override operator fun invoke(value: T) : Boolean {
        for (cb in callbacks) {
            cb.invoke(value)

            if (value.isCancelled) {
                return false
            }
        }
        return true
    }

    override fun clone() = ContinuousEventCallback<T>()
        .also { clone ->
            clone.callbacks.addAll(this.callbacks.map { it })
        }
}