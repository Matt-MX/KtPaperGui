package com.mattmx.ktgui.utils

/**
 * Utility interface that allows for us to invoke the self object.
 */
interface Invokable<T> {
    infix operator fun invoke(block: T.() -> Unit) = block.invoke(this as T)
}