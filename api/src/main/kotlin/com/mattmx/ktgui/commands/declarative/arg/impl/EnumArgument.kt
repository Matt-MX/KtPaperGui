package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext
import java.util.*

class EnumArgument<E : Enum<E>>(
    val enumClass: Class<E>,
    name: String,
    typeName: String
) : Argument<E>(name, typeName) {
    var toString: Optional<(E) -> String> = Optional.empty()
        private set

    init {
        this.consumes(ArgumentConsumer.single())
    }

    infix fun getStringValueOf(method: E.() -> String) = apply {
        this.toString = Optional.of(method)
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): E? {
        return if (toString.isEmpty) {
            enumClass.enumConstants.firstOrNull { toString.get()(it).equals(stringValue, true) }
        } else {
            enumClass.enumConstants.firstOrNull { it.name.equals(stringValue, true) }
        }
    }
}