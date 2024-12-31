package com.mattmx.ktgui.argument

import com.mattmx.ktgui.ArgumentConsumer
import com.mattmx.ktgui.command.CommandInvocation
import kotlin.properties.ReadOnlyProperty

open class MultiChoiceArgument<T>(
    name: String,
    val mapProvider: () -> Map<String, T>
) : Argument<T>(name) {
    override var consumer = ArgumentConsumer.single()

    override fun parse(invocation: CommandInvocation<*>, value: String): Result<T> {
        val values = mapProvider()

        return values[value].let { value ->
            if (value == null) Result.failure(RuntimeException()) else Result.success(value)
        }
    }
}

fun <T> multiChoiceArgument(
    map: Map<String, T>
) = delegatedArgument(MultiChoiceArgument(DELEGATED_ARGUMENT_NAME) { map })

fun <T> multiChoiceArgument(
    list: List<Pair<String, T>>
): ReadOnlyProperty<Any?, MultiChoiceArgument<T>> {
    val listMap = list.toMap()
    return delegatedArgument(MultiChoiceArgument(DELEGATED_ARGUMENT_NAME) { listMap })
}

fun <T> multiChoiceArgument(
    vararg args: Pair<String, T>
) = multiChoiceArgument(args.toList())

fun <T> multiChoiceArgument(
    mapProvider: () -> Map<String, T>
) = delegatedArgument(MultiChoiceArgument(DELEGATED_ARGUMENT_NAME, mapProvider))

fun <T> multiChoiceArgumentList(
    mapProvider: () -> Collection<Pair<String, T>>
): ReadOnlyProperty<Any?, MultiChoiceArgument<T>> {
    return multiChoiceArgument { mapProvider().toMap() }
}