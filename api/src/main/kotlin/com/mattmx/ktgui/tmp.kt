package com.mattmx.ktgui

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

fun interface Listener<V> {
    fun update(oldValue: Any, newValue: Any)
}

val test = arrayListOf<Listener<*>>()
fun <V> ReadWriteProperty<Any?, V>.addListener(listener: Listener<V>) {
    test.add(listener)
}
fun <V : Any> state(initialValue: V) : ReadWriteProperty<Any?, V> {
    val readWriteProperty = Delegates.observable(initialValue) { prop, old, new ->
        test.forEach { it.update(old, new) }
    }
    return readWriteProperty
}

fun main() {
    val ref = state(arrayOf("MattMX"))
    var players by ref

    ref.addListener { old, new -> println("$old -> $new") }

    players += "test"
}