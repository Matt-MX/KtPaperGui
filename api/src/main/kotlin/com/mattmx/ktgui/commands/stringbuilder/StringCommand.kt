package com.mattmx.ktgui.commands.stringbuilder

import com.mattmx.ktgui.commands.stringbuilder.arg.Argument
import com.mattmx.ktgui.commands.stringbuilder.arg.ArgumentContext
import com.mattmx.ktgui.commands.stringbuilder.syntax.*
import com.mattmx.ktgui.commands.suggestions.SuggestionInvocation
import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import com.mattmx.ktgui.configuration.Configuration
import com.mattmx.ktgui.utils.JavaCompatibility
import org.bukkit.command.CommandSender
import java.util.Optional
import java.util.function.Consumer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class StringCommand<T : CommandSender>(
    val source: String
) {
    lateinit var name: String
    var aliases = arrayOf<String>()
    var subcommands = arrayOf<StringCommand<*>>()
    var expectedArguments = arrayOf<Argument<*>>()
    private var permission: Optional<(RunnableCommandContext<T>) -> Boolean> = Optional.empty()
    private var runs: Optional<(RunnableCommandContext<T>) -> Unit> = Optional.empty()
    private var missing: Optional<(MissingArgContext<T>) -> Unit> = Optional.empty()
    private var invalid: Optional<(InvalidArgContext<T>) -> Unit> = Optional.empty()

    init {
        val parsed = Parser(source).parse()

        for (syntax in parsed) {
            when (syntax) {
                is VariableDeclarationSyntax -> {
                    expectedArguments += Argument<Any>(
                        syntax.getName(),
                        syntax.getType(),
                        null,
                        !syntax.getType().isOptional
                    )
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

    infix fun invalid(block: InvalidArgContext<T>.() -> Unit) = apply {
        this.invalid = Optional.of(block)
    }

    infix fun runs(block: RunnableCommandContext<T>.() -> Unit) = apply {
        this.runs = Optional.of(block)
    }

    @JavaCompatibility
    fun runs(block: Consumer<RunnableCommandContext<T>>) = apply {
        this.runs = Optional.of {
            block.accept(it)
        }
    }

    infix fun args(block: ArgumentOptions<T>.() -> Unit) = apply {
        ArgumentOptions<T>(this).apply(block)
    }

    class ArgumentOptions<T : CommandSender>(private val command: StringCommand<T>) {
        operator fun String.invoke(block: Argument<*>.() -> Unit) =
            command.expectedArguments.firstOrNull { it.name() == this }
                ?.apply(block)
                ?: error("Unregistered argument '$this'.")
    }

    inline operator fun <V : CommandSender> String.invoke(block: StringCommand<V>.() -> Unit) =
        StringCommand<V>(this).let {
            // todo could also have parameters
            subcommands += it
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
            val value = if (arg.type().isVararg) {
                context.rawArgs.subList(index, context.rawArgs.size).joinToString(" ")
            } else context.rawArgs.getOrNull(index)

            if (arg.isRequired() && value == null) {
                val missingArgContext = MissingArgContext<T>(arg, context.rawArgs)
                missing.ifPresent { it.invoke(missingArgContext) }
                return
            } else {
                val actualValue = if (arg.suggests.isPresent) {
                    arg.suggests.get().getValue(value.toString())
                } else value

                if (actualValue == null) {
                    val invalidArgumentContext = InvalidArgContext<T>(arg, context.rawArgs)
                    invalid.ifPresent { it.invoke(invalidArgumentContext) }
                    return
                }

                argumentValues[arg.name()] = arg.createContext(value, actualValue)
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
    fun getUsage(options: CommandUsageOptions = CommandUsageOptions()): String {
        var builder = "${options.namePrefix}$name${options.gap}"
        if (subcommands.isNotEmpty())
            builder += subcommands.joinToString(options.subCommands.divider) { subcommand -> subcommand.name }
        else {
            var end = ""
            builder += expectedArguments.joinToString(" ") { arg ->

                val suggestions =
                    if (options.arguments.showSuggestions) {
                        val suggestions = arg.getDefaultSuggestions()
                        if (!suggestions.isNullOrEmpty()) {
                            val opt = options.arguments
                            "${opt.suggestionsChar}${opt.suggestionsPrefix}${suggestions.joinToString(opt.suggestionsDivider)}${opt.suggestionsSuffix}"
                        } else "${options.arguments.typeChar}${arg.type().typeName}${if (arg.type().isVararg) "..." else ""}"
                    } else "${options.arguments.typeChar}${arg.type().typeName}${if (arg.type().isVararg) "..." else ""}"

                // Apply descriptions
                if (options.arguments.showDescriptions) {
                    val extra =
                        if (arg.isRequired()) options.arguments.descriptionsRequired else options.arguments.descriptionsOptional
                    end += "\n${options.arguments.descriptionsPrefix}${arg.name()}${options.arguments.descriptionDivider}${arg.description()}$extra"
                }

                "${options.arguments.prefix}${arg.name()}${if (arg.isRequired()) options.arguments.required else options.arguments.optional}$suggestions${options.arguments.suffix}"
            }
            builder += end
        }
        return builder
    }
}

inline operator fun <T : CommandSender> String.invoke(block: StringCommand<T>.() -> Unit) =
    StringCommand<T>(this).apply(block)

fun <T : Any> argument(type: String, isVarArg: Boolean = false) = ReadOnlyProperty { ref: Nothing?, property: KProperty<*> ->
    Argument<T>(property.name, VariableType(type, isVarArg, false))
}