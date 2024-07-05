package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.invocation.RunnableCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import java.util.Optional

class OptionArgument<T : Any>(
    val sub: Argument<T>
) {
    // todo command auto builder permissions node
    var requiresCheck = Optional.empty<(StorageCommandContext<*>) -> Boolean>()
        private set
    var shownInSuggestions = true
        private set

    infix fun requires(context: StorageCommandContext<*>.() -> Boolean) = apply {
        this.requiresCheck = Optional.of(context)
    }

    infix fun requiresPermission(node: String) = requires {
        sender.hasPermission(node)
    }

    infix fun shownInSuggestions(value: Boolean) = apply {
        this.shownInSuggestions = value
    }

    fun chatName() = sub.name().replace("_", "-")
}