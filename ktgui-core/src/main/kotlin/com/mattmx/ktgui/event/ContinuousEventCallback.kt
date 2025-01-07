package com.mattmx.ktgui.event

class ContinuousEventCallback<T : ContinuousEvent> : EventCallback<T>() {

    override fun apply(value: T) {
        this.registered.forEach { callback ->
            callback.invoke(value)

            if (!value.shouldContinueCallback()) {
                return@forEach
            }
        }
    }

}