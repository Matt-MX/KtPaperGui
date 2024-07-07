package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.RunnableCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import java.util.*

class FlagArgument(
    name: String
) : Argument<Boolean>(name, "boolean") {
    // todo command auto builder permissions node
    var requiresCheck = Optional.empty<(StorageCommandContext<*>) -> Boolean>()
    var showsInSuggestions = true

    infix fun requires(context: StorageCommandContext<*>.() -> Boolean) = apply {
        this.requiresCheck = Optional.of(context)
    }

    infix fun requiresPermission(node: String) = requires {
        sender.hasPermission(node)
    }

    fun chatName() = name().replace("_", "-")

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder?,
        context: BaseCommandContext<*>?,
        split: List<String>?
    ): Boolean {
        // todo context should contain included flags/options
        return false
    }

}