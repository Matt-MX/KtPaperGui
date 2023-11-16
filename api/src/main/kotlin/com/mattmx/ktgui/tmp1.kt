package com.mattmx.ktgui

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun interface SignalListener<T> {
    fun onChange(new: T)
}

class Signal<T>(initial: T) : ReadWriteProperty<Any?, T> {
    private var value: T = initial
    private val listeners = arrayListOf<SignalListener<T>>()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return this.value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
        listeners.forEach { it.onChange(this.value) }
    }

    fun addListener(listener: SignalListener<T>) {
        this.listeners.add(listener)
    }

    operator fun invoke() = value

    operator fun invoke(newValue: T) {
        this.value = newValue
        listeners.forEach { it.onChange(this.value) }
    }

    fun mut(mutate: T.() -> Unit) {
        mutate(value)
        listeners.forEach { it.onChange(this.value) }
    }

    fun update(mutate: T.() -> T) {
        this.value = mutate(value)
        listeners.forEach { it.onChange(this.value) }
    }

}

operator fun <T> Signal<MutableList<T>>.plusAssign(value: T) = mut { add(value) }

fun main() {
    val some = Signal(mutableListOf<String>())

    println(some())
    some.addListener {
        println("new val: $it")
    }
    some += "hello"

    println(some())
}