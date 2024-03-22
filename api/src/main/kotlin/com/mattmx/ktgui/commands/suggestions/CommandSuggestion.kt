package com.mattmx.ktgui.commands.suggestions

fun interface CommandSuggestion {
    fun getSuggestion(invocation: SuggestionInvocation<*>) : List<String>?
}