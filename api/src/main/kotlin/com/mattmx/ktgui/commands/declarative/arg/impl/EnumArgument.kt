package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext
import java.util.*

class EnumArgument<E : Enum<E>>(
    val enumClass: Class<E>,
    name: String,
    typeName: String
) : Argument<E>(name, typeName, SingleArgumentConsumer()) {
    var stringMethod: Optional<(E) -> String> = Optional.empty()
        private set

    infix fun stringMethod(method: E.() -> String) = apply {
        this.stringMethod = Optional.of(method)
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): E? {
        return if (stringMethod.isEmpty) {
            enumClass.enumConstants.firstOrNull { stringMethod.get()(it).equals(stringValue, true) }
        } else {
            enumClass.enumConstants.firstOrNull { it.name.equals(stringValue, true) }
        }
    }
}