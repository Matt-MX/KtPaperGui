package com.mattmx.ktgui.commands.declarative


class CommandContext<S : CommandSender>(
    val sender: S,
    val args: Array<String>,
)