package com.mattmx.ktgui.commands.suggestions

import com.mattmx.ktgui.commands.suggestions.impl.OnlinePlayersCommandSuggestion
import java.util.*

object CommandSuggestionRegistry {
    private val suggestions = hashMapOf<String, CommandSuggestion>(
        "player" to OnlinePlayersCommandSuggestion()
    )

    fun register(type: String, suggests: CommandSuggestion) =
        suggestions.put(type, suggests)

    fun get(type: String) = Optional.ofNullable(suggestions[type])
}