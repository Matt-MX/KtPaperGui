package com.mattmx.ktgui.command.context

import com.mattmx.ktgui.argument.Argument
import com.mattmx.ktgui.argument.ArgumentContext

class RunnableCommandContext<T>(
    val sender: T,
    val raw: Array<String>,
    arguments: Map<Argument<*>, ArgumentContext<*>>
) : ArgumentProviderContext(arguments) {

    @Suppress("UNCHECKED_CAST")
    fun <T> getSenderAsOrNull() : T? {
        return sender as? T
    }

    fun <T> getSenderAs() = getSenderAsOrNull<T>()!!

}