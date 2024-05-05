package com.mattmx.ktgui.commands.stringbuilder

import com.mattmx.ktgui.commands.stringbuilder.arg.Argument
import org.bukkit.command.CommandSender

class InvalidArgContext<T : CommandSender>(
    val invalidArgument: Argument<*>,
    args: List<String>
) : RawCommandContext<T>(args) {

    operator fun Argument<*>.invoke(block: Argument<*>.() -> Unit) {
        if (invalidArgument.name() == name()) {
            block.invoke(this)
        }
    }

}