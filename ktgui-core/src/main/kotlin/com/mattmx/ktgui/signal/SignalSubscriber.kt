package com.mattmx.ktgui.signal

fun interface SignalSubscriber<T> {

    fun onUpdate(newValue: T)

}