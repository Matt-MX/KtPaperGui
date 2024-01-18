package com.mattmx.ktgui.components.signal

fun interface SignalListener<T> {
    fun onChange(value: T)
}