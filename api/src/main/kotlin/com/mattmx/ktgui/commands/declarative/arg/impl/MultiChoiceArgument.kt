package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.suggests
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class MultiChoiceArgument<T : Any>(
    name: String,
    initialChoices: () -> Map<String, T>
) : Argument<T>(name, "multi-choice") {
    private val choices: () -> Map<String, T> = initialChoices

    init {
        this.consumes(
            ArgumentConsumer.until { argumentProcessor, s ->
                choices().containsKey(s.joinToString(" "))
            }
        )
        suggests { choices().keys.toList() }
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder?,
        context: BaseCommandContext<*>?,
        stringValue: String?
    ) = choices()[stringValue]
}