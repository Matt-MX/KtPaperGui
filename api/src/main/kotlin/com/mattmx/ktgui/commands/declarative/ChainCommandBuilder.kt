package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.commands.declarative.arg.Argument
import org.bukkit.command.CommandSender

class ChainCommandBuilder(val name: String) {
    val arguments = arrayListOf<Argument<*>>()
    val subcommands = arrayListOf<DeclarativeCommandBuilder<*>>()

    inline fun <reified T : CommandSender> build() = DeclarativeCommandBuilder(name, T::class.java).apply {
        this.expectedArguments += arguments
        this.subcommands += this@ChainCommandBuilder.subcommands
    }
}

inline operator fun <reified T : CommandSender> ChainCommandBuilder.invoke(block: DeclarativeCommandBuilder<T>.() -> Unit) =
    build<T>().apply(block)

operator fun String.div(argument: Argument<*>) = ChainCommandBuilder(this).apply {
    arguments.add(argument)
}

operator fun String.div(argument: List<Argument<*>>) = ChainCommandBuilder(this).apply {
    arguments.addAll(argument)
}

operator fun String.div(subs: List<DeclarativeCommandBuilder<*>>) = ChainCommandBuilder(this).apply {
    subcommands.addAll(subs)
}

operator fun DeclarativeCommandBuilder<*>.plus(other: DeclarativeCommandBuilder<*>) = listOf(this, other)

operator fun ChainCommandBuilder.div(argument: Argument<*>) = this.apply {
    arguments.add(argument)
}