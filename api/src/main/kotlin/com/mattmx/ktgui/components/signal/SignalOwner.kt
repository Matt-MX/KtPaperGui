package com.mattmx.ktgui.components.signal

interface SignalOwner {
    fun <V> addDependency(signal: Signal<V>)
}