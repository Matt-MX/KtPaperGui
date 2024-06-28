package com.mattmx.ktgui.commands.suggestions.impl

import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class OnlinePlayersCommandSuggestion : CommandSuggestion<Player> {
    override fun getSuggestion(invocation: SuggestionInvocation<*>): List<String> {
        return Bukkit.getOnlinePlayers()
            .filter { if (invocation.sender.orElse(null) is Player) (invocation.sender.get() as Player).canSee(it) else true }
            .map { it.name }
            .toList()
    }

    override fun getValue(argumentString: String): Player? {
        return Bukkit.getPlayer(argumentString)
    }
}