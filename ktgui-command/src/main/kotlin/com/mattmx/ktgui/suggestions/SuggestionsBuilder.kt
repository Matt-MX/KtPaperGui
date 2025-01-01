package com.mattmx.ktgui.suggestions

import com.mattmx.ktgui.command.CommandInvocation

open class SuggestionsBuilder<T : SuggestionsBuilder<T>> {
    val suggestions = mutableSetOf<String>()
    var finalFilter: (invoc: CommandInvocation<*>, suggestion: String) -> Boolean = { _, _ -> true }

    fun add(suggestion: String) = apply {
        this.suggestions.add(suggestion)
    } as T

    fun add(vararg suggestion: String) = apply {
        this.suggestions.addAll(suggestion)
    } as T

    fun add(suggestions: Collection<String>) = apply {
        this.suggestions.addAll(suggestions)
    } as T

    fun addIf(suggestion: String, condition: () -> Boolean) = apply {
        if (condition()) {
            add(suggestion)
        }
    } as T

    fun filter(filter: (invoc: CommandInvocation<*>, suggestion: String) -> Boolean) = apply {
        this.finalFilter = filter
    } as T

    fun filterLastArgStartsWith() = apply {
        this.finalFilter = { invoc, suggestion ->
            suggestion.startsWith(invoc.args.lastOrNull() ?: "", true)
        }
    } as T

    fun getSuggestions(invocation: CommandInvocation<*>): List<String> {
        return suggestions.filter { suggestion -> finalFilter.invoke(invocation, suggestion) }
    }
}