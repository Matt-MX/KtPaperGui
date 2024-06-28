package com.mattmx.ktgui.components.signal

interface GuiSignalOwner<T : SignalListener<*>> : SignalOwner {
    var currentlyProcessing: T?

    override fun <V> addDependency(signal: Signal<V>) {
        if (currentlyProcessing == null) return
        signal.addDependency(currentlyProcessing!! as SignalListener<V>)
    }
}