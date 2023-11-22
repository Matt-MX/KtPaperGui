package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.configuration.Configuration
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class KtCommandBuilder<S : CommandSender>(val name: String) {
    val aliases = arrayOf<String>()
    val subcommands = arrayOf<KtCommandBuilder<*>>()
    var expectedArguments = arrayOf<Argument<S, *, *>>()
    private lateinit var permission: (CommandContext<S>) -> Boolean
    private lateinit var runs: (CommandContext<S>) -> Unit

    fun permission(block: CommandContext<S>.() -> Boolean) = apply { permission = block }
    fun runs(block: CommandContext<S>.() -> Unit) = apply { runs = block }
    open fun register() {

    }

    fun getSuggestions(context: CommandContext<S>): List<String> {
        val currentArgument = getCurrentArgument(context)
        val suggestions = currentArgument?.suggests?.invoke(context) ?: return emptyList()
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
    private fun getCurrentArgument(context: CommandContext<S>): Argument<S, *, *>? {
        if (expectedArguments.isEmpty()) return null
        // Greedy arguments will eat the rest of the arguments
        if (expectedArguments.first().type == ArgumentType.GREEDY) return expectedArguments.first()

        repeat(expectedArguments.size) { argIndex ->
            val registered = expectedArguments.getOrNull(argIndex) ?: return null
            val comparing = context.args.getOrNull(argIndex) ?: return null
            // todo need to think about optional args!!! wtf do we do there
        }

        return null
    }

    /**
     * Used to find the current sub-command
     *
     * @param args of the current command invocation
     * @param sender command sender
     * @return the current sub-command or null if it is invalid
     */
    private fun getCurrentSubcommand(context: CommandContext<S>): KtCommandBuilder<*>? {
        if (subcommands.isEmpty()) return null
        val firstArg = context.args.getOrNull(0)
        return subcommands.firstOrNull { it.name == firstArg || it.aliases.contains(firstArg) }
    }

    private lateinit var context: CommandContext<S>

    operator fun <T, V> Argument<S, T, V>.provideDelegate(
        thisRef: Any?,
        property: KProperty<*>,
    ): ReadOnlyProperty<T, V> {
        id = property.name

        return ReadOnlyProperty { thisRef, property -> context.getValue(this) as V }
    }

    operator fun invoke(context: CommandContext<S>) {
        // save values of args
        this.context = context
        val values = expectedArguments.map { it.id to it.getValue(context) }
        runs.invoke(context.withValues(values))
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
                if (showDescriptions && arg.description() != null) {
                    val extra = when (arg.type) {
                        ArgumentType.REQUIRED_SINGLE -> "(Required)"
                        ArgumentType.OPTIONAL_SINGLE -> "(Optional)"
                        else -> "(Sentence)"
                    }
                    end += "\n> ${arg.id} - ${arg.description()} $extra"
                }

                when (arg.type) {
                    ArgumentType.REQUIRED_SINGLE -> "<${arg.id}!$suggestions>"
                    ArgumentType.OPTIONAL_SINGLE -> "<${arg.id}?$suggestions>"
                    else -> "<${arg.id}...>"
                }
            }
            builder += end
        }
        return builder
    }
}