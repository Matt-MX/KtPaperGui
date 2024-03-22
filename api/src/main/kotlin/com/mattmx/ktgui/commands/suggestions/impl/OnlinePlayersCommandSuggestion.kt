package com.mattmx.ktgui.commands.suggestions.impl

import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import com.mattmx.ktgui.commands.suggestions.SuggestionInvocation
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class OnlinePlayersCommandSuggestion : CommandSuggestion {
    override fun getSuggestion(invocation: SuggestionInvocation<*>): List<String> {
        return Bukkit.getOnlinePlayers()
            .filter { if (invocation.sender is Player) invocation.sender.canSee(it) else true }
            .map { it.name }
            .toList()
    }
}