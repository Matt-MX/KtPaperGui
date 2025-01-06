package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import java.util.concurrent.CompletableFuture

class OptionHolderArgumentType(
    private val expected: List<ArgumentWrapper<*>>
) : CustomArgumentType<OptionHolderArgumentType.Result, String> {

    override fun parse(reader: StringReader): Result {
        val values = hashMapOf<ArgumentWrapper<*>, Any>()
        // TODO(matt): doesn't currently support multiple options
        val matcher = OPTION_PREFIX_SYNTAX.toPattern().matcher(reader.string)

        while (matcher.find()) {
            val optionAndValue = matcher.group()
            val (optionString, stringValue) = optionAndValue.split(" ").let { args ->
                args.first() to args.subList(1, args.size).joinToString(" ")
            }

            val option = expected.firstOrNull { "--${it.name}" == optionString }
                ?: error("Unknown option $optionString!")

            val parsedValue = option.argumentType.parse(StringReader(stringValue))

            if (parsedValue != null) {
                values[option] = parsedValue
            }
        }

        while (reader.canRead()) {
            reader.skip()
        }

        return Result(values)
    }

    override fun getExamples(): MutableCollection<String> {
        return expected.map { arg -> "--${arg.name} ${arg.argumentType.examples}" }.toMutableSet()
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        // TODO(matt): impl suggestions

        return super.listSuggestions(context, builder)
    }

    override fun getNativeType() = StringArgumentType.greedyString()

    class Result(
        val map: Map<ArgumentWrapper<*>, Any>
    )

    companion object {
        private val OPTION_PREFIX_SYNTAX = "--\\w[\\w-]+\\s.+"
    }
}