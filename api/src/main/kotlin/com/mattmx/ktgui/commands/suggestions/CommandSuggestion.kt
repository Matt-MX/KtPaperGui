package com.mattmx.ktgui.commands.suggestions

fun interface CommandSuggestion<V> {
    fun getSuggestion(invocation: SuggestionInvocation<*>): List<String>?

    fun getLastArgSuggestion(invocation: SuggestionInvocation<*>) = getSuggestion(invocation)
        ?.filter { it.startsWith((invocation.rawArgs.lastOrNull() ?: "")) }

    fun getValue(argumentString: String) : V? {
        return argumentString as V?
    }

}