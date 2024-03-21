package com.mattmx.ktgui.commands.stringbuilder

import org.bukkit.command.CommandSender

open class RawCommandContext<T : CommandSender>(
    val rawArgs: List<String>
) {

}