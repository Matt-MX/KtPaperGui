package com.mattmx.ktgui.event

open class EventCallback<T> {
    open val registered = mutableListOf<(T) -> Unit>()

    open operator fun invoke(block: T.() -> Unit) {
        this.registered.add(block)
    }

    open fun apply(value: T) {
        this.registered.forEach { callback -> callback.invoke(value) }
    }

    open fun remove(callback: (T) -> Unit) = registered.remove(callback)

    open fun clear() = this.registered.clear()
}