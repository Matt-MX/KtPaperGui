package com.mattmx.ktgui.commands.stringbuilder

import com.mattmx.ktgui.commands.stringbuilder.arg.Argument
import org.bukkit.command.CommandSender

class MissingArgContext<T : CommandSender>(
    val missingArgument: Argument<*>,
    args: List<String>
) : RawCommandContext<T>(args)