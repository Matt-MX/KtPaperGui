package com.mattmx.ktgui.papi

import com.mattmx.ktgui.commands.declarative.arg.Argument
import org.bukkit.entity.Player

class PlaceholderParseContext(
    val requestedBy: Player?,
    val params: List<String>
) {

    operator fun <T : Any> Argument<T>.invoke(): T = TODO()

}