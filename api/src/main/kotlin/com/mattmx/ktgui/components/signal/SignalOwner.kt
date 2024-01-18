package com.mattmx.ktgui.components.signal

interface SignalOwner {
    fun <T, V> addDependency(signal: Signal<T, V>)
}