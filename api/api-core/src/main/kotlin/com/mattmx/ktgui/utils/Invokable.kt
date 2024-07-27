package com.mattmx.ktgui.utils

interface Invokable<T> {
    operator fun invoke(block: T.() -> Unit) = block.invoke(this as T)
}