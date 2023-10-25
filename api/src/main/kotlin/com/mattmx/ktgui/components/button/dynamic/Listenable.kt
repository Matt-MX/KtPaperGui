package com.mattmx.ktgui.components.button.dynamic

class Listenable<T : Any>(private var value: T) {
    private val listeners = arrayListOf<VariableListener>()

    fun get() = value
    fun mut(modify: T.() -> Unit) : T {
        modify(value)
        update()
        return value
    }

    fun set(newValue: T) {
        value = newValue
        update()
    }

    fun update() {
        listeners.toMutableList().forEach { it.onChange(value) }
    }

    fun addListener(listener: VariableListener) {
        this.listeners.add(listener)
    }

    fun removeListener(listener: VariableListener) {
        this.listeners.remove(listener)
    }

    override fun toString(): String {
        return value.toString()
    }

}