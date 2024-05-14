package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.invocation.InvalidArgContext
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import com.mattmx.ktgui.commands.declarative.syntax.VariableType
import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import com.mattmx.ktgui.commands.suggestions.CommandSuggestionRegistry
import com.mattmx.ktgui.event.EventCallback
import com.mattmx.ktgui.utils.Invokable
import java.util.*

class Argument<T : Any>(
    private var name: String,
    private val type: VariableType,
    var description: String? = null,
    private val required: Boolean = true
) : Invokable<Argument<T>> {
    var suggests = Optional.empty<CommandSuggestion<T>>()
    val missingCallback = EventCallback<InvalidArgContext<*>>()
    val invalidCallback = EventCallback<InvalidArgContext<*>>()

    init {
        withTypeSuggestions()
    }

    fun name() = name

    fun name(name: String) {
        this.name = name
    }

    fun description() = description ?: ""

    fun type() = type

    fun isRequired() = required

    infix fun missing(block: InvalidArgContext<*>.() -> Unit) = apply {
        this.missingCallback.callbacks.add(block)
    }

    infix fun invalid(block: InvalidArgContext<*>.() -> Unit) = apply {
        this.invalidCallback.callbacks.add(block)
    }

    fun invokeMissing(context: InvalidArgContext<*>): Boolean {
        missingCallback.invoke(context)
        return !missingCallback.isEmpty()
    }

    fun invokeInvalid(context: InvalidArgContext<*>): Boolean {
        invalidCallback.invoke(context)
        return !invalidCallback.isEmpty()
    }

    fun withTypeSuggestions() = apply {
        suggests = CommandSuggestionRegistry.get(type.typeName) as Optional<CommandSuggestion<T>>
    }

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

    infix fun nameEquals(other: Argument<*>) = name() == other.name()

    override fun toString() =
        "<$name${if (type.isOptional) "?" else ""}:${type.typeName}${if (type.isVararg) "..." else ""}>"
}

infix fun <T : Any> Argument<T>.suggests(suggest: CommandSuggestion<T>) = apply {
    this.suggests = Optional.of(suggest)
}

infix fun <T : Any> Argument<T>.suggestsTopLevel(suggest: CommandSuggestion<T>) = apply {
    this.suggests = Optional.of(suggest)
}