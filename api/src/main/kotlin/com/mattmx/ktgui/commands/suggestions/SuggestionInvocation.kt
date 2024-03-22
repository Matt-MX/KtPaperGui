package com.mattmx.ktgui.commands.suggestions

import org.bukkit.command.CommandSender
import java.util.*

class SuggestionInvocation<S : CommandSender>(
    val rawArgs: List<String>,
    val sender: Optional<S>
) {
    fun currentArgument() = rawArgs.lastOrNull() ?: ""
}