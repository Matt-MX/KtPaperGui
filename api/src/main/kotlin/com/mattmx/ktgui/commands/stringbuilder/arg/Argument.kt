package com.mattmx.ktgui.commands.stringbuilder.arg

import com.mattmx.ktgui.commands.stringbuilder.RawCommandContext
import com.mattmx.ktgui.commands.stringbuilder.syntax.VariableType
import com.mattmx.ktgui.commands.suggestions.CommandSuggestionRegistry
import com.mattmx.ktgui.commands.suggestions.SuggestionInvocation
import org.bukkit.command.CommandSender
import java.util.*

class Argument<T>(
    private val name: String,
    private val type: VariableType,
    var description: String? = null,
    private val required: Boolean = true
) {
    private var suggests = Optional.empty<(RawCommandContext<*>) -> List<String>?>()

    fun name() = name

    fun description() = description ?: ""

    fun type() = type

    fun isRequired() = required

    fun suggests(suggest: RawCommandContext<*>.() -> List<String>?) = apply {
        this.suggests = Optional.of(suggest)
    }

    fun suggestions() = suggests.orElse(null)

    fun getDefaultSuggestions() : List<String>? {
        return if (suggests.isPresent) {
            val context = RawCommandContext<CommandSender>(emptyList())
            suggests.get().invoke(context)
        } else {
            val suggestion = CommandSuggestionRegistry.get(type.typeName)
            val context = SuggestionInvocation(emptyList(), Optional.empty())
            if (suggestion.isPresent) suggestion.get().getSuggestion(context) else null
        }
    }

    enum class Type {
        SINGLE,
        GREEDY
    }

    operator fun invoke(block: Argument<T>) {}

    operator fun invoke() = ""

    override fun toString() = "<$name${if (type.isOptional) "?" else ""}:${type.typeName}${if (type.isVararg) "..." else ""}>"
}