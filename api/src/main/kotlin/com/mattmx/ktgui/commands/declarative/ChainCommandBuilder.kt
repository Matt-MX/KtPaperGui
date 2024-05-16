package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.MultiArgument
import com.mattmx.ktgui.utils.JavaCompatibility
import org.bukkit.command.CommandSender

class ChainCommandBuilder(val name: String) {
    val arguments = arrayListOf<Argument<*>>()
    val subcommands = arrayListOf<DeclarativeCommandBuilder>()

    @JavaCompatibility
    fun argument(argument: Argument<*>) = apply {
        arguments.add(argument)
    }

    @JavaCompatibility
    fun subcommand(vararg command: DeclarativeCommandBuilder) = apply {
        subcommands.addAll(command)
    }

    fun build() = build(DeclarativeCommandBuilder(name))

    fun <T : DeclarativeCommandBuilder> build(existing: T) = existing.apply {
        this.expectedArguments += arguments
        this.subcommands += this@ChainCommandBuilder.subcommands
    }
}

inline operator fun ChainCommandBuilder.invoke(block: DeclarativeCommandBuilder.() -> Unit) =
    build().apply(block)

val ChainCommandBuilder.command
    get() = build()

operator fun String.div(argument: Argument<*>) = ChainCommandBuilder(this).apply {
    arguments.add(argument)
}

@JvmName("div1")
operator fun String.div(argument: List<Argument<*>>) = ChainCommandBuilder(this).apply {
    arguments.add(MultiArgument("multi-argument", *argument.toTypedArray()))
}

operator fun String.div(subs: List<DeclarativeCommandBuilder>) = ChainCommandBuilder(this).apply {
    subcommands.addAll(subs)
}

operator fun DeclarativeCommandBuilder.plus(other: DeclarativeCommandBuilder) = listOf(this, other)

operator fun ChainCommandBuilder.div(argument: Argument<*>) = this.apply {
    arguments.add(argument)
}