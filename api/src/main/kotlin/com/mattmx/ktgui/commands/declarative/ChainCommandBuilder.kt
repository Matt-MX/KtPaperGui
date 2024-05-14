package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.commands.declarative.arg.Argument
import org.bukkit.command.CommandSender

class ChainCommandBuilder(val name: String) {
    val arguments = arrayListOf<Argument<*>>()

    inline fun <reified T : CommandSender> build() = DeclarativeCommandBuilder(name, T::class.java).apply {
        this.expectedArguments += arguments
    }
}

inline operator fun <reified T : CommandSender> ChainCommandBuilder.invoke(block: DeclarativeCommandBuilder<T>.() -> Unit) =
    build<T>().apply(block)

operator fun String.div(argument: Argument<*>) = ChainCommandBuilder(this).apply {
    arguments.add(argument)
}

operator fun ChainCommandBuilder.div(argument: Argument<*>) = this.apply {
    arguments.add(argument)
}