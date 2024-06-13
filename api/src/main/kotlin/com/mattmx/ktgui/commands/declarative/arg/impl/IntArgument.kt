package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class IntArgument(
    name: String,
    typeName: String
) : Argument<Int>(name, typeName, SingleArgumentConsumer()) {
    var min: Int = Int.MIN_VALUE
    var max: Int = Int.MAX_VALUE

    override fun validate(stringValue: String?): Boolean {
        stringValue ?: return isOptional()

        val intValue = stringValue.toIntOrNull() ?: return false

        return intValue in (min..max)
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): Int? {
        return stringValue?.toIntOrNull()
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