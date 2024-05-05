package com.mattmx.ktgui.commands.stringbuilder.arg

import java.util.*

class ArgumentContext<T : Any>(
    private val stringValue: String?,
    private val value: Optional<T>,
    private val argument: Argument<T>
) {
    fun argument() = argument

    fun isEmpty() = value.isEmpty

    fun getOrNull(): T? = value.orElse(null)

    fun orElse(other: T?) = value.orElse(other)

    fun stringValue() = stringValue

    fun optional() = value

    override fun toString() = getOrNull().toString()
}