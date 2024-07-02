package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.consumers.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.suggests
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class MultiChoiceArgument<T : Any>(
    name: String,
    initialChoices: HashMap<String, T>
) : Argument<T>(name, "multi-choice") {
    private val choices = initialChoices

    init {
        this.consumes(ArgumentConsumer.until { argumentProcessor, s -> choices.containsKey(s) })
        suggests { choices.keys.toList() }
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ) = choices[stringValue]
}