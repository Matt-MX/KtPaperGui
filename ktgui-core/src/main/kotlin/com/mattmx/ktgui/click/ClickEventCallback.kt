package com.mattmx.ktgui.click

import com.mattmx.ktgui.event.EventCallback

class ClickEventCallback<P, T : ClickButtonEvent>(
    private val parent: P
) {
    private val callbacks = hashMapOf<ClickType, EventCallback<T>>()

    operator fun invoke(block: ClickEventCallback<P, T>.() -> Unit): P {
        this.apply(block)
        return parent
    }

    operator fun ClickType.plus(other: ClickType) = arrayOf(this, other)
    operator fun Array<ClickType>.plus(other: ClickType) = this + arrayOf(other)

    operator fun Collection<ClickType>.invoke(block: T.() -> Unit) = handle(this, block)

    @JvmName("invokeClickTypeArray")
    operator fun Array<ClickType>.invoke(block: T.() -> Unit) = handle(this.toList(), block)

    @JvmName("invokeClickType")
    operator fun ClickType.invoke(block: T.() -> Unit) = handle(listOf(this), block)

    operator fun invoke(clickType: ClickType, callback: T.() -> Unit) = handle(arrayOf(clickType), callback)

    @JvmName("invokeCollection1")
    operator fun invoke(clickType: Collection<ClickType>, callback: T.() -> Unit) = handle(clickType, callback)

    fun handle(vararg types: ClickType, callback: T.() -> Unit) =
        handle(types.toList(), callback)

    @JvmName("handle1")
    fun handle(types: Array<ClickType>, callback: T.() -> Unit) =
        handle(types.toList(), callback)

    fun handle(types: Collection<ClickType>, callback: T.() -> Unit): P {
        for (type in types) {
            callbacks.computeIfAbsent(type) { EventCallback() }.invoke(callback)
        }

        return parent
    }

    fun apply(event: T) {
        if (!event.shouldContinueEventCallback()) return
        val callback = callbacks[event.getClickType()] ?: return

        for (func in callback.registered) {
            func.invoke(event)
            if (!event.shouldContinueEventCallback()) return
        }
    }

}