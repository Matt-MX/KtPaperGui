package com.mattmx.ktgui.signal

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Signal<V>(
    private var value: V,
    block: Signal<V>.() -> Unit
) : ReadWriteProperty<Any?, V> {
    val receivers = mutableListOf<SignalSubscriber<V>>()

    init {
        block.invoke(this)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) : V {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        receivers.forEach { receiver -> receiver.onUpdate(value) }
        this.value = value
    }
}