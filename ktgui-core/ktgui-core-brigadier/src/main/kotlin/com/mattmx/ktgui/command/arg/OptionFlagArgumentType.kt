package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestion
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

class OptionFlagArgumentType(
    private val expected: List<ArgumentWrapper<*>>,
    private val prefix: String = "--"
) : ArgumentType<OptionFlagArgumentType.Result> {
    private val optionPrefixSyntax = "$prefix[\\w-]*".toRegex()

    override fun parse(reader: StringReader): Result {
        val values = hashMapOf<ArgumentWrapper<*>, Any>()

        while (reader.canRead(2)) {
            if (reader.read() == '-' && reader.read() == '-') {
                val optionName = reader.readUnquotedString()
                val option = expected.firstOrNull { it.name == optionName }
                    ?: error("Unknown option named '${optionName}'")

                reader.skipWhitespace()

                if (isBoolean(option)) {
                    values[option] = true
                } else {
                    val value = option.argumentType.parse(reader)
                        ?: error("Invalid value for option '${optionName}'")

                    values[option] = value
                }
            }
        }

        return Result(values)
    }

    override fun getExamples(): MutableCollection<String> {
        return expected.map { arg -> "$prefix${arg.name} ${arg.argumentType.examples}" }.toMutableSet()
    }

    fun isBoolean(arg: ArgumentWrapper<*>): Boolean {
        return Boolean::class.javaObjectType.isAssignableFrom(arg.clazz)
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return CompletableFuture.supplyAsync {
            val args = builder.remaining.split(" ")
            val lastIndex = args.lastIndex

            var indexOfOption = lastIndex

            while (indexOfOption >= 0) {
                val currentString = args.getOrNull(indexOfOption)
                    ?: break

                if (!currentString.matches(optionPrefixSyntax)) {
                    indexOfOption--
                } else {
                    break
                }
            }

            val lastArgument = args.getOrNull(lastIndex) ?: ""
            var remainingWithoutLast = args.subList(0, lastIndex).joinToString(" ")

            if (remainingWithoutLast.isNotBlank()) {
                remainingWithoutLast += " "
            }

            if (indexOfOption == lastIndex) {
                for (option in expected) {

                    // Make sure it starts with this arg
                    val appendedArgName = "$prefix${option.name}"
                    if (!appendedArgName.startsWith(lastArgument, true)) {
                        continue
                    }

                    builder.suggest(remainingWithoutLast + prefix + option.name)
                }
            } else {
                val optionName = args.getOrNull(indexOfOption)
                    ?: return@supplyAsync builder.buildFuture().join()
                val option = expected.firstOrNull { expectedArg ->
                    expectedArg.name == optionName.replaceFirst(prefix, "")
                } ?: return@supplyAsync builder.buildFuture().join()

                return@supplyAsync option.argumentType.listSuggestions(context, builder)
                    .thenApply { suggestions ->
                        Suggestions.create(
                            context.input,
                            suggestions.list
                                .filter { suggestion ->
                                    suggestion.text.startsWith(lastArgument, true)
                                }
                                .map { suggestion ->
                                    Suggestion(
                                        suggestion.range,
                                        remainingWithoutLast + suggestion.text,
                                        suggestion.tooltip
                                    )
                                })
                    }.join()
            }

            builder.build()
        }
    }

    class Result(
        val map: Map<ArgumentWrapper<*>, Any>
    )
}