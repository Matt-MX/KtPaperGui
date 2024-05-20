package com.mattmx.ktgui.event

/**
 * Stores a list of callbacks for [T], will invoke all of them.
 */
open class EventCallback<T>(
    val callbacks: ArrayList<T.() -> Unit> = arrayListOf()
) {

    operator fun invoke(block: T.() -> Unit) {
        callbacks.add(block)
    }

    open operator fun invoke(value: T) : Boolean {
        callbacks.forEach { it.invoke(value) }
        return true
    }

    fun clear() = callbacks.clear()

    fun isEmpty() = callbacks.isEmpty()

    fun first() = callbacks.first()

    open fun clone() = EventCallback(arrayListOf(*callbacks.map { it }.toTypedArray()))

}