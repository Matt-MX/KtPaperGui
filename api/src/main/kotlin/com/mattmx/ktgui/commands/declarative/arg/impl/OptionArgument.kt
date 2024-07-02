package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.arg.Argument

class OptionArgument<T : Any>(
    val sub: Argument<T>
) {
    fun chatName() = sub.name().replace("_", "-")
}