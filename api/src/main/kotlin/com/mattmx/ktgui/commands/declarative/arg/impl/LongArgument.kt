package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class LongArgument(
    name: String,
    typeName: String
) : Argument<Long>(name, typeName) {
    var min: Long = Long.MIN_VALUE
    var max: Long = Long.MAX_VALUE

    init {
        this.consumes(ArgumentConsumer.single())
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder?,
        context: BaseCommandContext<*>?,
        stringValue: String?
    ): Long? {
        val longValue = stringValue?.toLongOrNull() ?: return null
        return if (longValue in (min..max)) longValue else null
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