package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.consumer.GreedyArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.consumers.ArgumentConsumer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

const val DELEGATED_ARG_NAME = "delegated_arg"

fun <T : Any> argument(type: String, isVarArg: Boolean = false) =
    delegateArgument(argument<T>(type, DELEGATED_ARG_NAME))

fun <T : Argument<*>> delegateArgument(arg: T) = ReadOnlyProperty { ref: Nothing?, property: KProperty<*> ->
    arg.apply {
        name(property.name)
    }
}

fun <T : Any> argument(type: String, name: String) = Argument<T>(name, type)

fun stringArgument(type: String = "string") =
    delegateArgument(StringArgument(DELEGATED_ARG_NAME, type))

fun greedyStringArgument(type: String = "string") =
    delegateArgument(StringArgument(DELEGATED_ARG_NAME, type).apply { this.consumes(ArgumentConsumer.remaining()) })

fun intArgument(type: String = "int") =
    delegateArgument(IntArgument(DELEGATED_ARG_NAME, type))

fun longArgument(type: String = "long") =
    delegateArgument(LongArgument(DELEGATED_ARG_NAME, type))

fun doubleArgument(type: String = "double") =
    delegateArgument(DoubleArgument(DELEGATED_ARG_NAME, type))

fun relativeCoords(type: String = "coords") =
    delegateArgument(RelativeCoordinateArgument(DELEGATED_ARG_NAME, type))

fun playerArgument() =
    delegateArgument(OnlinePlayerArgument(DELEGATED_ARG_NAME))

inline fun <reified E : Enum<E>> enumArgument(type: String = E::class.java.simpleName) =
    delegateArgument(EnumArgument(E::class.javaObjectType, DELEGATED_ARG_NAME, type))

fun flag() = delegateArgument(FlagArgument(DELEGATED_ARG_NAME))

fun <T : Any> multiChoiceArgument(vararg choices: Pair<String, T>) =
    delegateArgument(MultiChoiceArgument(DELEGATED_ARG_NAME, hashMapOf(*choices)))

fun <T : Any> simpleMappedArgument() =
    delegateArgument(SimpleArgument<T>(DELEGATED_ARG_NAME, "type"))