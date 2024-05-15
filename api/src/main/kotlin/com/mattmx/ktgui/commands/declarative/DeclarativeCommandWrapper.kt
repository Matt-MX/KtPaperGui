package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import com.mattmx.ktgui.commands.wrapper.CommandWrapper
import org.bukkit.command.CommandSender
import java.util.*

class DeclarativeCommandWrapper(
    name: String,
    val builder: DeclarativeCommandBuilder
) : CommandWrapper(name) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        val context = StorageCommandContext(sender, commandLabel, args.toList())

        builder.invoke(context)
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val context = SuggestionInvocation(Optional.of(sender), alias, args.toList())

        val (newContext, finalCommand) = builder.getCurrentCommand(context)
        val outArgs = finalCommand?.getSuggestions(newContext)

        return outArgs?.toMutableList() ?: mutableListOf()
    }

    override fun testPermission(target: CommandSender): Boolean {
        val context = StorageCommandContext(target, name, listOf())

        return builder.permission.orElse(null)?.invoke(context) ?: false
    }

}