package com.mattmx.ktgui.commands.suggestions

import com.mattmx.ktgui.commands.stringbuilder.FakePlayer
import com.mattmx.ktgui.commands.suggestions.impl.OnlinePlayersCommandSuggestion
import java.util.*

object CommandSuggestionRegistry {
    private val suggestions = hashMapOf<String, CommandSuggestion<*>>(
        "player" to OnlinePlayersCommandSuggestion(),
        "fakePlayer" to object : CommandSuggestion<FakePlayer> {
            private val players = listOf(FakePlayer("MattMX"), FakePlayer("GabbySimon"))

            override fun getSuggestion(invocation: SuggestionInvocation<*>) = players.map { it.name }

            override fun getValue(argumentString: String): FakePlayer? = players.firstOrNull { it.name == argumentString }
        }
    )

    fun register(type: String, suggests: CommandSuggestion<*>) =
        suggestions.put(type, suggests)

    fun get(type: String) = Optional.ofNullable(suggestions[type])
}