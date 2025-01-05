package com.mattmx.ktgui.command

fun command(name: String, block: DeclarativeCommand.() -> Unit) =
    DeclarativeCommand(name).apply(block)