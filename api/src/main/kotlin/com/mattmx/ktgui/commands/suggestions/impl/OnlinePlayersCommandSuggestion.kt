package com.mattmx.ktgui.commands.suggestions.impl

import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class OnlinePlayersCommandSuggestion : CommandSuggestion<Player> {
    override fun getSuggestion(invocation: StorageCommandContext<*>): List<String> {
        return Bukkit.getOnlinePlayers()
            .filter { if (invocation.sender is Player) invocation.sender.canSee(it) else true }
            .map { it.name }
            .toList()
    }

    override fun getValue(argumentString: String?): Player? {
        argumentString ?: return null
        return Bukkit.getPlayer(argumentString)
    }
}