package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.invocation.RunnableCommandContext
import org.bukkit.command.CommandSender

class ProvidedArgsBlock(
    val context: RunnableCommandContext<*>
) {
    var hasAlreadyRan = false

    infix fun or(block: () -> Unit) = apply {
        if (hasAlreadyRan) return@apply

        invoke(block)
    }

    fun withArgs(vararg args: Argument<*>, block: () -> Unit) = apply {
        if (hasAlreadyRan) return@apply

        if (args.all { context.getArgumentContext<Any>(it.name())?.isPresent() == true }) {
            invoke(block)
        }
    }

    private fun invoke(block: () -> Unit) {
        hasAlreadyRan = true
        block()
    }
}

fun <T : CommandSender> RunnableCommandContext<T>.withArgs(vararg args: Argument<*>, block: () -> Unit) =
    ProvidedArgsBlock(this).apply {
        withArgs(*args, block = block)
    }