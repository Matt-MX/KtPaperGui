package com.mattmx.ktgui.commands.suggestions

import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation

fun interface CommandSuggestion<V> {
    fun getSuggestion(invocation: SuggestionInvocation<*>): List<String>?

    fun getLastArgSuggestion(invocation: SuggestionInvocation<*>) = getSuggestion(invocation)
        ?.filter { it.startsWith((invocation.rawArgs.lastOrNull() ?: ""), true) }

    fun getValue(argumentString: String?) : V? {
        return argumentString as V?
    }

}