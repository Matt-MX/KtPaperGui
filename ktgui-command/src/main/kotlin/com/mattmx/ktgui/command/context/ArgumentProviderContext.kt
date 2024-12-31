package com.mattmx.ktgui.command.context

import com.mattmx.ktgui.argument.Argument
import com.mattmx.ktgui.argument.ArgumentContext

open class ArgumentProviderContext(
    val arguments: Map<Argument<*>, ArgumentContext<*>?>
) {

    fun Argument<*>.isPresent() = arguments.containsKey(this)

    fun <T> Argument<T>.getValue() = arguments[this]?.value as T

    fun <T> Argument<T>.getValueOrNull() = arguments[this]?.value as? T

    fun <T> Argument<T>.getValueOrElse(other: T) = getValueOrNull() ?: other

    fun <T> Argument<T>.getContext() = arguments[this] ?: ArgumentContext(null, "empty")

    operator fun <T> Argument<T>.invoke() = getValue()

}