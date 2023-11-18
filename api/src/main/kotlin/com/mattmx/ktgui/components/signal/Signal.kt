package com.mattmx.ktgui.components.signal

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Signal<T, V>(initial: V, private val owner: SignalOwner) : ReadWriteProperty<T, V> {
    private var value: V = initial
    private val listeners = arrayListOf<SignalListener<V>>()

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        owner.addDependency(this)
        return this.value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        this.value = value
        invokeListeners()
    }

    fun addListener(listener: SignalListener<V>) {
        this.listeners.add(listener)
    }

    operator fun invoke() = value.apply {
        owner.addDependency(this@Signal)
    }

    infix operator fun invoke(newValue: V) {
        this.value = newValue
        invokeListeners()
    }

    infix fun mut(mutate: V.() -> Unit) {
        mutate(value)
        invokeListeners()
    }

    infix fun setTo(mutate: V.() -> V) {
        this.value = mutate(value)
        invokeListeners()
    }

    infix fun setTo(newValue: V) {
        this.value = newValue
        invokeListeners()
    }

    fun invokeListeners() {
        listeners.toMutableList().forEach { it.onChange(this.value) }
    }

    fun addDependency(signalListener: SignalListener<V>) {
        if (this.listeners.contains(signalListener)) return
        this.listeners += signalListener
    }
}