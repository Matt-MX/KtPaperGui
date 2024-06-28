package com.mattmx.ktgui.commands.suggestions

import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation

fun interface CommandSuggestion<V> {
    fun getSuggestion(invocation: SuggestionInvocation<*>): List<String>?

    fun getLastArgSuggestion(invocation: SuggestionInvocation<*>) = getSuggestion(invocation)
        ?.filter { it.startsWith((invocation.rawArgs.lastOrNull() ?: ""), true) }

<<<<<<< HEAD
    fun getValue(argumentString: String): V? {
=======
    fun getValue(argumentString: String?) : V? {
>>>>>>> fc760191aa5090e9dac6c3014739a12dc7fc5dfb
        return argumentString as V?
    }

}