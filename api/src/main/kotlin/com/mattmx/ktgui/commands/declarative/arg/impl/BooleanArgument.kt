package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.suggests
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class BooleanArgument(
    name: String,
    typeName: String
) : Argument<Boolean>(name, typeName) {

    init {
        suggests { listOf("true", "false") }
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): Boolean? {
        return stringValue?.toBooleanStrictOrNull()
    }

}