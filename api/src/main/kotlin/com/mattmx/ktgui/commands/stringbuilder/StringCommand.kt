package com.mattmx.ktgui.commands.stringbuilder

import com.mattmx.ktgui.commands.stringbuilder.arg.Argument
import com.mattmx.ktgui.commands.stringbuilder.arg.ArgumentContext
import com.mattmx.ktgui.commands.stringbuilder.syntax.CommandDeclarationSyntax
import com.mattmx.ktgui.commands.stringbuilder.syntax.Parser
import com.mattmx.ktgui.commands.stringbuilder.syntax.SubCommandDeclarationSyntax
import com.mattmx.ktgui.commands.stringbuilder.syntax.VariableDeclarationSyntax
import com.mattmx.ktgui.configuration.Configuration
import org.bukkit.command.CommandSender
import java.util.Optional

class StringCommand<T : CommandSender>(
    source: String
) {
    lateinit var name: String
    var aliases = arrayOf<String>()
    var subcommands = arrayOf<StringCommand<*>>()
    var expectedArguments = arrayOf<Argument<*>>()
    private var permission: Optional<(RunnableCommandContext<T>) -> Boolean> = Optional.empty()
    private var runs: Optional<(RunnableCommandContext<T>) -> Unit> = Optional.empty()
    private var missing: Optional<(MissingArgContext<T>) -> Unit> = Optional.empty()

    init {
        val parsed = Parser(source).parse()

        for (syntax in parsed) {
            when (syntax) {
                is VariableDeclarationSyntax -> {
                    expectedArguments += Argument<Any>(syntax.getName(), syntax.getType(), null, !syntax.getType().isOptional)
                }
                is CommandDeclarationSyntax -> {
                    // todo should be top level only
                    name = syntax.getName()
                }
                is SubCommandDeclarationSyntax -> {
                    name = syntax.getName()
                }
            }
        }
    }

    infix fun missing(block: MissingArgContext<T>.() -> Unit) = apply {
        this.missing = Optional.of(block)
    }

    infix fun runs(block: RunnableCommandContext<T>.() -> Unit) = apply {
        this.runs = Optional.of(block)
    }

    inline operator fun <V : CommandSender> String.invoke(block: StringCommand<V>.() -> Unit) =
        StringCommand<V>(this).let {
            // todo could also have parameters
            subcommands += it
        }

    fun getSuggestions(context: RawCommandContext<T>): List<String> {
        val currentArgument = getCurrentArgument(context)
        val suggestions = currentArgument?.suggestions()?.invoke(context) ?: return emptyList()
        val lastArgument = context.rawArgs.lastOrNull() ?: ""
        return suggestions.filter { suggestion -> suggestion.startsWith(lastArgument, true) }.toList()
    }

    /**
     * Used to find the current argument, presuming we are on the current sub-command
     *
     * @param args the current arguments of the command invocation
     * @param sender command sender
     * @return the current argument or null if it is invalid
     */
    private fun getCurrentArgument(context: RawCommandContext<T>): Argument<*>? {
        if (expectedArguments.isEmpty()) return null

        // Greedy arguments will eat the rest of the arguments todo
        if (expectedArguments.first().type().isVararg) return expectedArguments.first()

        repeat(expectedArguments.size) { argIndex ->
            val registered = expectedArguments.getOrNull(argIndex) ?: return null
            val comparing = context.rawArgs.getOrNull(argIndex) ?: return null
            // todo need to think about optional args!!! wtf do we do there
        }

        return null
    }

    operator fun unaryPlus() = register()

    fun register() = apply {

    }

    fun invoke(context: RawCommandContext<T>) {

        // Set variables
        val argumentValues = hashMapOf<String, ArgumentContext<*>>()
        for ((index, arg) in expectedArguments.withIndex()) {
            // todo var offset for sub-commands?
            val value = context.rawArgs.getOrNull(index)

            if (arg.isRequired() && value == null) {
                val missingArgContext = MissingArgContext<T>(arg, context.rawArgs)
                missing.ifPresent { it.invoke(missingArgContext) }
                return
            } else {
                argumentValues[arg.name()] = ArgumentContext(Optional.ofNullable(value), arg as Argument<String>)
            }
        }

        val runnableContext = RunnableCommandContext<T>(argumentValues, context.rawArgs)
        runs.ifPresent { it.invoke(runnableContext) }
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
                    if (it.isNotEmpty() && it.size <= maxArgumentOptionsDisplayed) " = [" + it.joinToString("|") + "]"
                    else ":${arg.type().typeName}"
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
    StringCommand<T>(this).apply(block)