package com.mattmx.ktgui.commands.suggestions

import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext

fun interface CommandSuggestion<V> {
    fun getSuggestion(invocation: StorageCommandContext<*>): Collection<String>?

    fun getLastArgSuggestion(invocation: StorageCommandContext<*>) = getSuggestion(invocation)
        ?.filter { it.startsWith((invocation.rawArgs.lastOrNull() ?: ""), true) }

    fun getValue(argumentString: String?) : V? {
        return argumentString as V?
    }

}