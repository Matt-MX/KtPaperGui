package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class IntArgument(
    name: String,
    typeName: String
) : Argument<Int>(name, typeName) {
    var min: Int = Int.MIN_VALUE
    var max: Int = Int.MAX_VALUE

    init {
        this.consumes(ArgumentConsumer.single())
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder?,
        context: BaseCommandContext<*>?,
        stringValue: String?
    ): Int? {
        val intValue = stringValue?.toIntOrNull() ?: return null
        return if (intValue in (min..max)) intValue else null
    }

    infix fun range(range: IntRange) = apply {
        this.min = range.first
        this.max = range.last
    }

    infix fun min(min: Int) = apply {
        this.min = min
    }

    infix fun max(max: Int) = apply {
        this.max = max
    }

}