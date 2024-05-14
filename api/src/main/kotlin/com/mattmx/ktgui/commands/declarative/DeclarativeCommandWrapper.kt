package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import com.mattmx.ktgui.commands.wrapper.CommandWrapper
import org.bukkit.command.CommandSender
import java.util.*

class DeclarativeCommandWrapper<T : CommandSender>(
    name: String,
    val builder: DeclarativeCommandBuilder<T>
) : CommandWrapper(name) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        val context = StorageCommandContext(sender, commandLabel, args.toList())

        builder.invokeAnySender(context)
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val context = SuggestionInvocation(Optional.of(sender), alias, args.toList())

        val (newContext, finalCommand) = builder.getCurrentCommandAnySender(context)
        val outArgs = finalCommand?.getSuggestions(newContext)

        return outArgs?.toMutableList() ?: mutableListOf()
    }

}