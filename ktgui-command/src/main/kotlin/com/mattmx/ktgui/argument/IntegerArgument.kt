package com.mattmx.ktgui.argument

import com.mattmx.ktgui.ArgumentConsumer
import com.mattmx.ktgui.command.CommandInvocation
import com.mattmx.ktgui.command.context.RunnableCommandContext

class IntegerArgument(
    name: String,
    var range: IntRange = (Int.MIN_VALUE..Int.MAX_VALUE)
) : Argument<Int>(name) {
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

    infix fun incorrectRange(handle: RunnableCommandContext<*>.() -> Unit) {
        this.incorrectRange = handle
    }

    override var consumer = ArgumentConsumer.single()

    override fun parse(invocation: CommandInvocation<*>, value: String): Result<Int> {
        val intResult = runCatching { value.toInt() }

        if (intResult.isFailure) {
            return intResult
        }

        if (range.isEmpty()) {
            return intResult
        }

        return if (intResult.isSuccess && intResult.getOrThrow() in range) {
            intResult
        } else {
            Result.failure(RuntimeException())
        }
    }

}

fun intArgument(
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    range: IntRange = (min..max),
    block: (IntegerArgument.() -> Unit)? = null
) = delegatedArgument(IntegerArgument(DELEGATED_ARGUMENT_NAME, range).also { block?.invoke(it) })