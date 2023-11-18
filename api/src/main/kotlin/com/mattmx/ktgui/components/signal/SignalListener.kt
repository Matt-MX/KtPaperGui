package com.mattmx.ktgui.components.signal

interface SignalListener<T> {
    fun onChange(value: T)
}