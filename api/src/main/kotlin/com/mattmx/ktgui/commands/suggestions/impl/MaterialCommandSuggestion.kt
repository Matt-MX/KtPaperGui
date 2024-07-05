package com.mattmx.ktgui.commands.suggestions.impl

import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import org.bukkit.Material

class MaterialCommandSuggestion : CommandSuggestion<Material> {
    override fun getSuggestion(invocation: StorageCommandContext<*>): List<String> {
        return Material.values().map { it.key.toString() }
    }

    override fun getValue(argumentString: String?): Material? {
        return Material.values().firstOrNull { it.key.toString() == argumentString }
    }
}