package com.mattmx.ktgui.commands.stringbuilder.arg

import com.mattmx.ktgui.commands.stringbuilder.RawCommandContext
import com.mattmx.ktgui.commands.stringbuilder.syntax.VariableType
import org.bukkit.command.CommandSender
import java.util.*

class Argument<T>(
    private val name: String,
    private val type: VariableType,
    private val description: String? = null,
    private val required: Boolean = true
) {
    private var suggests = Optional.empty<(RawCommandContext<*>) -> List<String>?>()

    fun name() = name

    fun description() = description ?: ""

    fun type() = type

    fun isRequired() = required

    fun withSuggestions(suggest: RawCommandContext<*>.() -> List<String>?) = apply {
        this.suggests = Optional.of(suggest)
    }

    fun suggestions() = suggests.orElse(null)

    fun getDefaultSuggestions() = if (suggests.isPresent) {
        val context = RawCommandContext<CommandSender>(emptyList())
        suggests.get().invoke(context)
    } else listOf()

    enum class Type {
        SINGLE,
        GREEDY
    }
}