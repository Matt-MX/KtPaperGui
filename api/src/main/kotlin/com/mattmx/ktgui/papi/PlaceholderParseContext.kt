package com.mattmx.ktgui.papi

import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentContext
import org.bukkit.entity.Player

class PlaceholderParseContext(
    val requestedBy: Player?,
    val params: List<String>,
    val providedArguments: HashMap<String, ArgumentContext<*>>
) {

    operator fun <T : Any> Argument<T>.invoke(): T = context.getOrNull()!!

    val <T : Any> Argument<T>.context
        get() = providedArguments[name()] as ArgumentContext<T>? ?: ArgumentContext.empty(this)

}