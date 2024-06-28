package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.arg.Argument

class OptionArgument<T : Any>(
    name: String,
    typeName: String
) : Argument<T>(name, typeName) {

    fun chatName() = name().replace("_", "-")


}