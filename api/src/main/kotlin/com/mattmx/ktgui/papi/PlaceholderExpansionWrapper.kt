package com.mattmx.ktgui.papi

import com.mattmx.ktgui.commands.declarative.ChainCommandBuilder
import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.div
import com.mattmx.ktgui.utils.JavaCompatibility
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.clip.placeholderapi.expansion.Relational
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class PlaceholderExpansionWrapper(
    val owner: JavaPlugin
) : PlaceholderExpansion(), Relational {
    private val placeholders = arrayListOf<Placeholder>()
    private val parser = PlaceholderParser(this)
    var id = owner.name
        private set
    var _persists = false
        private set
    var _author = owner.pluginMeta.authors.joinToString(", ")
        private set
    var _version = owner.pluginMeta.version
        private set
    var splitArgs = { params: String -> params.split("_") }
        private set
    var requiresPredicate = Optional.empty<() -> Boolean>()
    var isDebug = false

    override fun getIdentifier() = id
    override fun getAuthor() = _author
    override fun getVersion() = _version
    override fun getPlaceholders() = placeholders.map { it.toString() }.toMutableList()
    fun getPlaceholdersList() = placeholders
    override fun persist() = _persists
    override fun canRegister() = if (requiresPredicate.isPresent) requiresPredicate.get()() else true

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

    infix fun persists(persists: Boolean) = apply {
        this._persists = persists
    }

    infix fun requires(predicate: () -> Boolean) = apply {
        this.requiresPredicate = Optional.of(predicate)
    }

    infix fun registerPlaceholder(placeholder: Placeholder) = placeholders.add(placeholder)

    @JavaCompatibility
    infix fun withPlaceholder(placeholder: Placeholder) = apply {
        placeholders.add(placeholder)
    }

    @JavaCompatibility
    infix fun withPlaceholder(builder: Placeholder.Builder) = apply {
        placeholders.add(builder.build(this))
    }

    operator fun Argument<*>.div(other: Argument<*>) =
        ChainCommandBuilder(Placeholder.EMPTY_PLACEHOLDER)
            .div(this)
            .div(other)

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        val paramsSplit = splitArgs(params)
        val (placeholder, args) = parser.parse(player, params) ?: return null

        val context = PlaceholderParseContext(player, paramsSplit, args)
        val result = placeholder.parse(context)

        if (result != null) {
            return result.toString()
        }
        return null
    }

    override fun onPlaceholderRequest(one: Player, two: Player, params: String): String? {
        val paramsSplit = splitArgs(params)
        val (placeholder, args) = parser.parse(one, params) ?: return null

        val context = RelationalPlaceholderParseContext(one, two, paramsSplit, args)
        val result = placeholder.parseRelationally(context)

        if (result != null) {
            return result.toString()
        }
        return null
    }

    companion object {
        val emptyCommand = DeclarativeCommandBuilder("null")
    }
}