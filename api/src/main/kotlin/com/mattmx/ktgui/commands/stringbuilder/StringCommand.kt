package com.mattmx.ktgui.commands.stringbuilder

import com.mattmx.ktgui.commands.CommandInvocation
import com.mattmx.ktgui.commands.declarative.ArgumentType
import com.mattmx.ktgui.commands.stringbuilder.arg.Argument
import com.mattmx.ktgui.configuration.Configuration
import org.bukkit.command.CommandSender

class StringCommand<T : CommandSender> {
    lateinit var name: String
    var aliases = arrayOf<String>()
    var subcommands = arrayOf<StringCommand<*>>()
    var expectedArguments = arrayOf<Argument<*>>()
    private lateinit var permission: (CommandContext<T>) -> Boolean
    private lateinit var runs: (CommandContext<T>) -> Unit
    private lateinit var missing: (CommandContext<T>) -> Unit

    init {
        // todo parse params
        name = "test"
    }

    infix fun missing(block: CommandContext<T>.() -> Unit) = apply {
        this.missing = block
    }

    infix fun runs(block: CommandContext<T>.() -> Unit) = apply {
        this.runs = block
    }

    inline operator fun <V : CommandSender> String.invoke(block: StringCommand<V>.() -> Unit) =
        StringCommand<V>().let {
            // todo could also have parameters
            subcommands += it
        }

    fun getSuggestions(context: CommandContext<T>): List<String> {
        val currentArgument = getCurrentArgument(context)
        val suggestions = currentArgument?.suggestions()?.invoke(context) ?: return emptyList()
        val lastArgument = context.args.lastOrNull() ?: ""
        return suggestions.filter { suggestion -> suggestion.startsWith(lastArgument, true) }.toList()
    }

    /**
     * Used to find the current argument, presuming we are on the current sub-command
     *
     * @param args the current arguments of the command invocation
     * @param sender command sender
     * @return the current argument or null if it is invalid
     */
    private fun getCurrentArgument(context: CommandContext<T>): Argument<*>? {
        if (expectedArguments.isEmpty()) return null
        // Greedy arguments will eat the rest of the arguments
        if (expectedArguments.first().type() == Argument.Type.GREEDY) return expectedArguments.first()

        repeat(expectedArguments.size) { argIndex ->
            val registered = expectedArguments.getOrNull(argIndex) ?: return null
            val comparing = context.args.getOrNull(argIndex) ?: return null
            // todo need to think about optional args!!! wtf do we do there
        }

        return null
    }

    operator fun unaryPlus() = register()

    fun register() = apply {

    }

    /**
     * Builds a usage for this command.
     * You can create your own method with the [Configuration] class.
     *
     * @return a formatted string for usage of the command
     */
    fun getUsage(showDescriptions: Boolean = false, maxArgumentOptionsDisplayed: Int = 5): String {
        var builder = "$name "
        if (subcommands.isNotEmpty())
            builder += subcommands.joinToString("|") { subcommand -> subcommand.name }
        else {
            var end = ""
            builder += expectedArguments.joinToString(" ") { arg ->
                val suggestions = arg.getDefaultSuggestions()?.let {
                    if (it.size <= maxArgumentOptionsDisplayed) " = [" + it.joinToString("|") + "]"
                    else " = [...]"
                } ?: ""

                // Apply descriptions
                if (showDescriptions) {
                    val extra = if (arg.isRequired()) "(Required)" else "(Optional)"
                    end += "\n> ${arg.name()} - ${arg.description()} $extra"
                }

                if (arg.isRequired()) {
                    "<${arg.name()}!$suggestions>"
                } else "<${arg.name()}?$suggestions>"
            }
            builder += end
        }
        return builder
    }
}

inline operator fun <T : CommandSender> String.invoke(block: StringCommand<T>.() -> Unit) =
    StringCommand<T>().apply(block)