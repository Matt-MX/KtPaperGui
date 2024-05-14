package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.syntax.VariableType
import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import com.mattmx.ktgui.commands.suggestions.CommandSuggestionRegistry
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import java.util.*

class Argument<T : Any>(
    private val name: String,
    private val type: VariableType,
    var description: String? = null,
    private val required: Boolean = true
) {
    var suggests = Optional.empty<CommandSuggestion<T>>()
        private set

    init {
        withTypeSuggestions()
    }

    fun name() = name

    fun description() = description ?: ""

    fun type() = type

    fun isRequired() = required

    infix fun suggests(suggest: CommandSuggestion<T>) = apply {
        this.suggests = Optional.of(suggest)
    }

    fun withTypeSuggestions() = apply {
        suggests = CommandSuggestionRegistry.get(type.typeName) as Optional<CommandSuggestion<T>>
    }

    fun suggestions() = suggests.orElse(null)

    fun createContext(stringValue: String?, actualValue: Any?): ArgumentContext<T> {
        return ArgumentContext(stringValue, Optional.ofNullable(actualValue as T?), this)
    }

    fun getDefaultSuggestions(): List<String>? {
        val context = SuggestionInvocation(Optional.empty(), "", emptyList())
        return if (suggests.isPresent) {
            suggests.get().getSuggestion(context)
        } else {
            val suggestion = CommandSuggestionRegistry.get(type.typeName)
            if (suggestion.isPresent) suggestion.get().getSuggestion(context) else null
        }
    }

    fun nameEquals(other: Argument<*>) = name() == other.name()

    enum class Type {
        SINGLE,
        GREEDY
    }

    override fun toString() =
        "<$name${if (type.isOptional) "?" else ""}:${type.typeName}${if (type.isVararg) "..." else ""}>"
}