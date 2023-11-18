package com.mattmx.ktgui.commands.smart

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class CommandContext<S : CommandSender>(
    val sender: S,
    val args: Array<String>,
)