package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.arg.consumer.GreedyArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

const val DELEGATED_ARG_NAME = "delegated_arg"

fun <T : Any> argument(type: String, isVarArg: Boolean = false) =
    delegateArgument(argument<T>(type, DELEGATED_ARG_NAME, isVarArg))

fun <T : Argument<*>> delegateArgument(arg: T) = ReadOnlyProperty { ref: Nothing?, property: KProperty<*> ->
    arg.apply {
        name(property.name)
    }
}

fun <T : Any> argument(type: String, name: String, isVarArg: Boolean) =
    Argument<T>(name, type, if (isVarArg) GreedyArgumentConsumer() else SingleArgumentConsumer())

fun stringArgument(type: String = "string", isVarArg: Boolean = false) =
    delegateArgument(StringArgument(DELEGATED_ARG_NAME, type, if (isVarArg) GreedyArgumentConsumer() else SingleArgumentConsumer()))

fun intArgument(type: String = "int") =
    delegateArgument(IntArgument(DELEGATED_ARG_NAME, type))

fun doubleArgument(type: String = "double") =
    delegateArgument(DoubleArgument(DELEGATED_ARG_NAME, type))

fun relativeCoords(type: String = "coords") =
    delegateArgument(RelativeCoordinateArgument(DELEGATED_ARG_NAME, type))