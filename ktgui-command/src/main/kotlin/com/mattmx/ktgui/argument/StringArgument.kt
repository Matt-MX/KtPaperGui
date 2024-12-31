package com.mattmx.ktgui.argument

import com.mattmx.ktgui.ArgumentConsumer
import com.mattmx.ktgui.command.CommandInvocation
import com.mattmx.ktgui.command.context.RunnableCommandContext

class StringArgument(
    name: String,
    var range: IntRange = (Int.MIN_VALUE..Int.MAX_VALUE),
    var regex: Regex? = null
) : Argument<String>(name) {
    override var consumer = ArgumentConsumer.single()
    var max
        get() = range.max()
        set(value) {
            range = (min..value)
        }
    var min
        get() = range.min()
        set(value) {
            range = (value..max)
        }
    var incorrectRange: (RunnableCommandContext<*>) -> Unit = {}

    override fun parse(invocation: CommandInvocation<*>, value: String): Result<String> {
        return Result.success("")
    }
}

fun stringArgument(
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    range: IntRange = (min..max),
    regex: Regex? = null,
    block: (StringArgument.() -> Unit)? = null
) = delegatedArgument(StringArgument(DELEGATED_ARGUMENT_NAME, range, regex).also { block?.invoke(it) })