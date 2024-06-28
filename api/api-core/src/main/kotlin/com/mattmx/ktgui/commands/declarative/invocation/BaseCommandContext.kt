package com.mattmx.ktgui.commands.declarative.invocation

import com.mattmx.ktgui.utils.not
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

open class BaseCommandContext<T : CommandSender>(
    val sender: T,
    val alias: String,
    val rawArgs: List<String>
) {

    fun reply(component: Component) {
        sender.sendMessage(component)
    }

    fun reply(any: Any) {
        reply(!any.toString())
    }

    open fun clone(newList: List<String> = rawArgs): BaseCommandContext<T> {
        return BaseCommandContext(sender, alias, newList)
    }
}