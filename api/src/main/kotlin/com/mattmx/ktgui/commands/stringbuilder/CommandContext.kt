package com.mattmx.ktgui.commands.stringbuilder

import org.bukkit.command.CommandSender

class CommandContext<T : CommandSender>(
    val args: List<String>
) {

}