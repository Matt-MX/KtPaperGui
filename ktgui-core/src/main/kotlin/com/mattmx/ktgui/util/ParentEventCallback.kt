package com.mattmx.ktgui.util

class ParentEventCallback<T, P>(
    private val owner: P
) {
    val registered = mutableListOf<(T) -> Unit>()

    operator fun invoke(block: T.() -> Unit) : P {
        this.registered.add(block)
        return owner
    }

    fun apply(value: T) {
        this.registered.forEach { callback -> callback.invoke(value) }
    }

    fun clear() = this.registered.clear()
}