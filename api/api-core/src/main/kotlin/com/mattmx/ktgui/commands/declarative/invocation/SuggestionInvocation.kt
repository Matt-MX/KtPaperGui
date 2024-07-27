package com.mattmx.ktgui.commands.declarative.invocation

import org.bukkit.command.CommandSender
import java.util.*

class SuggestionInvocation<S : CommandSender>(
    val sender: Optional<S>,
    val alias: String,
    val rawArgs: List<String>
) {
    val last: String
        get() = rawArgs.lastOrNull() ?: ""

    fun clone(newList: List<String> = rawArgs): SuggestionInvocation<S> {
        return SuggestionInvocation(sender, alias, newList)
    }
}