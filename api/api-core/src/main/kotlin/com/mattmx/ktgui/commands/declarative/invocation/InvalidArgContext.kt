package com.mattmx.ktgui.commands.declarative.invocation

import com.mattmx.ktgui.commands.declarative.arg.Argument
import org.bukkit.command.CommandSender

class InvalidArgContext<T : CommandSender>(
    sender: T,
    alias: String,
    rawArgs: List<String>,
    val argument: Argument<*>,
    val provided: String?
) : StorageCommandContext<T>(sender, alias, rawArgs) {
    var invokeCallbackOnlyOnce = true
    private var invoked = false

    operator fun <V : Any> Argument<V>.invoke(block: Argument<V>.() -> Unit) {
        if (invoked) return

        if (argument.name() == name()) {
            block.invoke(this)
            if (invokeCallbackOnlyOnce)
                invoked = true
        }
    }

}