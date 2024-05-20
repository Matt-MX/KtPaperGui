package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class LongArgument(
    name: String,
    typeName: String
) : Argument<Long>(name, typeName, SingleArgumentConsumer()) {
    var min: Long = 0
    var max: Long = Long.MAX_VALUE

    override fun validate(stringValue: String?): Boolean {
        stringValue ?: return false

        val intValue = stringValue.toIntOrNull() ?: return false

        return intValue in (min..max)
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): Long? {
        return stringValue?.toLongOrNull()
    }

    infix fun range(range: LongRange) = apply {
        this.min = range.first
        this.max = range.last
    }

    infix fun min(min: Long) = apply {
        this.min = min
    }

    infix fun max(max: Long) = apply {
        this.max = max
    }

}