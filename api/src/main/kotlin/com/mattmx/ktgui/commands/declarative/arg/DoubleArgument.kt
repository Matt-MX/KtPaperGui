package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class DoubleArgument(
    name: String,
    typeName: String
) : Argument<Double>(name, typeName, SingleArgumentConsumer()) {
    var min: Double = 0.0
    var max: Double = Double.MAX_VALUE

    override fun validate(stringValue: String?): Boolean {
        stringValue ?: return false

        val doubleValue = stringValue.toDoubleOrNull() ?: return false

        return doubleValue in (min..max)
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): Double? {
        return stringValue?.toDoubleOrNull()
    }

    infix fun range(range: ClosedRange<Double>) = apply {
        this.min = range.start
        this.max = range.endInclusive
    }

    infix fun min(min: Double) = apply {
        this.min = min
    }

    infix fun max(max: Double) = apply {
        this.max = max
    }

}