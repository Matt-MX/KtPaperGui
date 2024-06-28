package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.consumer.GreedyArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import net.kyori.adventure.bossbar.BossBar.Flag
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
    delegateArgument(
        StringArgument(
            DELEGATED_ARG_NAME,
            type,
            if (isVarArg) GreedyArgumentConsumer() else SingleArgumentConsumer()
        )
    )

fun greedyStringArgument(type: String = "string") =
    stringArgument(type, true)

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

fun <T : Any> simpleArgument(type: String = "") =
    delegateArgument(SimpleArgument<T>(DELEGATED_ARG_NAME, type, SingleArgumentConsumer()))

inline fun <reified E : Enum<E>> enumArgument(type: String = E::class.java.simpleName) =
    delegateArgument(EnumArgument(E::class.javaObjectType, DELEGATED_ARG_NAME, type))

fun flag() = delegateArgument(FlagArgument(DELEGATED_ARG_NAME))

inline fun <reified T : Any> optionArgument(type: String = T::class.java.simpleName) =
    delegateArgument(OptionArgument<T>(DELEGATED_ARG_NAME, type, SingleArgumentConsumer()))