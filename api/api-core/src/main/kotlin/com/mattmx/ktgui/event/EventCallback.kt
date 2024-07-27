package com.mattmx.ktgui.event

open class EventCallback<T>(
    val callbacks: ArrayList<T.() -> Unit> = arrayListOf()
) {

    operator fun invoke(block: T.() -> Unit) {
        callbacks.add(block)
    }

    open operator fun invoke(value: T): Boolean {
        callbacks.forEach { it.invoke(value) }
        return true
    }

    fun clear() = callbacks.clear()

    fun isEmpty() = callbacks.isEmpty()

    open fun clone() = EventCallback(arrayListOf(*callbacks.map { it }.toTypedArray()))

}