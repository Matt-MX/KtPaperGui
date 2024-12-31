package com.mattmx.ktgui.command.context

import com.mattmx.ktgui.Flag
import com.mattmx.ktgui.argument.Argument
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

fun RunnableCommandContext<Audience>.reply(message: Component) {
    sender.sendMessage(message)
}

fun RunnableCommandContext<*>.runWithArgs(vararg args: Argument<*>, block: () -> Unit) : Unit? {
    if (args.all { arg -> arg.isPresent() }) {
        block()
        return Unit
    }
    return null
}

fun RunnableCommandContext<*>.runWithFlags(vararg flags: Flag, block: () -> Unit) : Unit? {
    TODO()
}

fun RunnableCommandContext<*>.runWithOptions(vararg options: Flag, block: () -> Unit) : Unit? {
    TODO()
}