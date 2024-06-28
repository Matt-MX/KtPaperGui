package com.mattmx.ktgui.papi

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PlaceholderExpansionWrapper(
    private val owner: JavaPlugin
) : PlaceholderExpansion() {
    private val placeholders = arrayListOf<Placeholder>()
    var id = owner.name
        private set
    var _author = owner.pluginMeta.authors.joinToString(", ")
        private set
    var _version = owner.pluginMeta.version
        private set
    var splitArgs = { params: String -> params.split("_") }
        private set

    override fun getIdentifier() = id
    override fun getAuthor() = _author
    override fun getVersion() = _version
    override fun getPlaceholders() = placeholders.map { it.toString() }.toMutableList()

    infix fun id(id: String) = apply {
        this.id = id
    }

    infix fun author(author: String) = apply {
        this._author = author
    }

    infix fun version(version: String) = apply {
        this._version = version
    }

    infix fun splitArgs(splitArgs: (String) -> List<String>) = apply {
        this.splitArgs = splitArgs
    }

    infix fun registerPlaceholder(placeholder: Placeholder) = placeholders.add(placeholder)

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        val context = PlaceholderParseContext(player, splitArgs(params))
        for (placeholder in placeholders.sortedByDescending { it.priority }) {
            val content = placeholder.parse(context)

            if (content != null) {
                return content.toString()
            }
        }
        return null
    }
}