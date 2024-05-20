package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.consumer.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext
import java.util.*

class SimpleArgument<T : Any>(
    name: String,
    typeName: String,
    consumer: ArgumentConsumer
) : Argument<T>(name, typeName, consumer) {
    var valueFromString = Optional.empty<(String) -> T?>()
        private set

    infix fun getValue(supplier: String.() -> T?) = apply {
        this.valueFromString = Optional.of(supplier)
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): T? {
        stringValue ?: return null
        return if (valueFromString.isPresent) valueFromString.get()(stringValue) else null
    }

}