package com.mattmx.ktgui.commands.stringbuilder.arg

import java.util.*

class ArgumentContext<T>(
    private val value: Optional<T>,
    private val argument: Argument<T>
) {
    fun argument() = argument

    fun isEmpty() = value.isEmpty

    fun getOrNull(): T? = value.orElse(null)

    fun optional() = value

    override fun toString() = getOrNull().toString()
}