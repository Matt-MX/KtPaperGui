package com.mattmx.ktgui.argument

import kotlin.properties.ReadOnlyProperty

const val DELEGATED_ARGUMENT_NAME = "delegate_arg"
fun <A, T : Argument<A>> delegatedArgument(
    arg: T
) : ReadOnlyProperty<Any?, T> {
    var initialized = false

    return ReadOnlyProperty { thisRef, property ->
        if (!initialized) {
            initialized = true
            arg.name = property.name
        }

        arg
    }
}