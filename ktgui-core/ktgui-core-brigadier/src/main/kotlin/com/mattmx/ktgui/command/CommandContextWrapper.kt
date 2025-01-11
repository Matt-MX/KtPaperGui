package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.ArgumentWrapper
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext

open class CommandContextWrapper<S>(
    val context: CommandContext<S>
) {
    val source: S
        get() = context.source
    val command: Command<S>
        get() = context.command
    val input: String
        get() = context.input

    inline fun <reified T> ArgumentWrapper<T>.orElse(default: T): T =
        runCatching { context.getArgument(name, T::class.java) }
            .getOrElse { default }

    val ArgumentWrapper<*>.isPresent
        get() = runCatching { context.getArgument(name, Any::class.java) }.getOrNull() != null

    inline operator fun <reified T> ArgumentWrapper<T>.invoke(): T = context.getArgument(name, T::class.java)
}