package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class FlagArgument(
    name: String
) : Argument<Boolean>(name, "boolean") {

    fun chatName() = name().replace("_", "-")

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        split: List<String>
    ): Boolean? {
        // todo context should contain included flags/options
        return false
    }

}