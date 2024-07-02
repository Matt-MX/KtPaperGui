package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.impl.OptionArgument
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.InvalidArgContext
import com.mattmx.ktgui.commands.declarative.invocation.RunnableCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import com.mattmx.ktgui.commands.suggestions.CommandSuggestionRegistry
import com.mattmx.ktgui.event.EventCallback
import com.mattmx.ktgui.utils.Invokable
import com.mattmx.ktgui.utils.JavaCompatibility
import java.util.*

open class Argument<T : Any>(
    private var name: String,
    private val typeName: String
) : Invokable<Argument<T>> {
    var description: String? = null
        protected set
    var consumer = ArgumentConsumer.single()
        protected set
    var suggests = Optional.empty<CommandSuggestion<T>>()
    var missingCallback = EventCallback<InvalidArgContext<*>>()
        protected set
    var invalidCallback = EventCallback<InvalidArgContext<*>>()
        protected set
    private var optional = false
    // todo impl default value

    init {
        withTypeSuggestions()
    }

    fun name() = name

    fun name(name: String) {
        this.name = name
    }

    fun description() = description ?: ""

    fun type() = typeName

    fun isRequired() = !optional

    fun isOptional() = optional

    fun required() = apply {
        this.optional = false
    }

    infix fun required(value: Boolean) = apply {
        this.optional = !value
    }

    fun optional() = apply {
        this.optional = true
    }

    infix fun optional(value: Boolean) = apply {
        this.optional = value
    }

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

    open fun getSuggestions() {
        TODO()
    }

    open fun getDefaultSuggestions(): Collection<String>? {
        val context = SuggestionInvocation(Optional.empty(), "", emptyList())
        return if (suggests.isPresent) {
            suggests.get().getSuggestion(context)
        } else {
            val suggestion = CommandSuggestionRegistry.get(typeName)
            if (suggestion.isPresent) suggestion.get().getSuggestion(context) else null
        }
    }

    fun withTypeSuggestions() = apply {
        suggests = CommandSuggestionRegistry.get(typeName) as Optional<CommandSuggestion<T>>
    }

    infix fun consumes(consumer: ArgumentConsumer) = apply {
        this.consumer = consumer
    }

    open fun consume(processor: ArgumentProcessor) = this.consumer.consume(processor)

    open fun getValueOfString(cmd: DeclarativeCommandBuilder, context: BaseCommandContext<*>, split: List<String>): T? {
        return getValueOfString(cmd, context, split.joinToString(" "))
    }

    open fun getValueOfString(cmd: DeclarativeCommandBuilder, context: BaseCommandContext<*>, stringValue: String?): T? {
        return if (cmd.localArgumentSuggestions.contains(name())) {
            cmd.localArgumentSuggestions[name()]?.getValue(stringValue) as T?
        } else if (suggests.isPresent) {
            suggests.get().getValue(stringValue)
        } else null
    }

    fun optionArgument() = OptionArgument(this)

    @JavaCompatibility
    fun getContext(context: RunnableCommandContext<*>) = context.getArgumentContext<T>(name());

    @JavaCompatibility
    fun getValue(context: RunnableCommandContext<*>) = context.getArgumentContext<T>(name())?.getOrNull()

    fun createContext(stringValue: String?, actualValue: Any?): ArgumentContext<T> {
        return ArgumentContext(stringValue, Optional.ofNullable(actualValue as T?), this)
    }

    infix fun nameEquals(other: Argument<*>) = name() == other.name()

    override fun toString() =
        "<$name${if (isOptional()) "?" else ""}:${typeName}}>"

    open fun applyToClone(cloned: Argument<T>) = cloned

    fun clone() : Argument<T> {
        return Argument<T>(name, typeName)
            .let {
                it.consumer = consumer
                it.invalidCallback = invalidCallback.clone()
                it.missingCallback = missingCallback.clone()
                it.suggests = suggests
                it.description = description
                applyToClone(it)
            }
    }
}

infix fun <T : Any> Argument<T>.suggests(suggest: CommandSuggestion<T>) = apply {
    this.suggests = Optional.of(suggest)
}

infix fun <T : Any> Argument<T>.suggestsTopLevel(suggest: CommandSuggestion<T>) = apply {
    this.suggests = Optional.of(suggest)
}