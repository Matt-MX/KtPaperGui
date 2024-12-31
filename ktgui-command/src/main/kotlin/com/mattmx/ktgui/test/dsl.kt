package com.mattmx.ktgui.test

import com.mattmx.ktgui.command.DeclarativeCommand

fun command(name: String, block: DeclarativeCommand.() -> Unit) =
    DeclarativeCommand(name).apply(block)