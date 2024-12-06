package com.mattmx.ktgui.util

class EventCallback<T> {
    val registered = mutableListOf<(T) -> Unit>()

    operator fun invoke(block: T.() -> Unit) {
        this.registered.add(block)
    }

    fun apply(value: T) {
        this.registered.forEach { callback -> callback.invoke(value) }
    }

    fun clear() = this.registered.clear()
}