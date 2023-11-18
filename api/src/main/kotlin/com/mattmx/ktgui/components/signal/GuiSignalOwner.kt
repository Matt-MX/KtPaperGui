package com.mattmx.ktgui.components.signal

interface GuiSignalOwner<T : SignalListener<*>> : SignalOwner {
    var currentlyProcessing: T?

    override fun <T, V> addDependency(signal: Signal<T, V>) {
        if (currentlyProcessing == null) return
        signal.addDependency(currentlyProcessing!! as SignalListener<V>)
    }
}