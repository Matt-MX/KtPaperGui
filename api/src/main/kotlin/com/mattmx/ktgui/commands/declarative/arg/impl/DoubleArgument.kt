package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class DoubleArgument(
    name: String,
    typeName: String
) : Argument<Double>(name, typeName) {
    var min: Double = -999999999999.0
    var max: Double = Double.MAX_VALUE

    init {
        this.consumes(ArgumentConsumer.single())
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): Double? {
        val doubleValue = stringValue?.toDoubleOrNull() ?: return null

        return if (doubleValue in (min..max)) doubleValue else null
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