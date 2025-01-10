package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import kotlin.properties.ReadOnlyProperty

inline fun <reified T : Any> custom(argumentType: ArgumentType<T>) = delegate(argumentType)

//val DEFAULT_INVALID_INPUT = { input: String -> "Invalid input '${input}'" }
//fun stringChoice(
//    vararg pairs: String,
//    invalid: (String) -> String = DEFAULT_INVALID_INPUT
//): ReadOnlyProperty<Any?, ArgumentWrapper<*, String>> {
//    return mapped(pairs.associateWith { it }, invalid = invalid)
//}
//
//inline fun <reified T : Any> mapped(
//    vararg pairs: Pair<String, T>,
//    noinline invalid: (String) -> String = DEFAULT_INVALID_INPUT
//): ReadOnlyProperty<Any?, ArgumentWrapper<*, T>> {
//    return mapped(pairs.toMap(), invalid = invalid)
//}
//
//inline fun <reified T : Any> mapped(
//    map: Map<String, T>,
//    noinline invalid: (String) -> String = DEFAULT_INVALID_INPUT
//) = custom(
//    customArgument<T, String>(StringArgumentType.word()) {
//        suggests { context, builder ->
//            map.keys.forEach(builder::suggest)
//            builder.buildFuture()
//        }
//        convert { map[it] ?: error(invalid(it)) }
//    }
//)

fun options(vararg option: ArgumentWrapper<*>) = delegate(OptionFlagArgumentType(option.toList()))

fun boolean() = delegate(BoolArgumentType.bool())
//fun prettyBoolean() = mapped<Boolean>(mapOf("on" to true, "off" to false))

fun string() = delegate(StringArgumentType.string())
fun word() = delegate(StringArgumentType.word())
fun greedyString() = delegate(StringArgumentType.greedyString())

fun int(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE) = delegate(IntegerArgumentType.integer(min, max))
fun long(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) = delegate(LongArgumentType.longArg(min, max))
fun float(min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE) = delegate(FloatArgumentType.floatArg(min, max))
fun double(min: Double = Double.MIN_VALUE, max: Double = Double.MAX_VALUE) =
    delegate(DoubleArgumentType.doubleArg(min, max))

inline fun <reified T : Any> delegate(type: ArgumentType<T>): ReadOnlyProperty<Any?, ArgumentWrapper<T>> {
    var instance: ArgumentWrapper<T>? = null

    return ReadOnlyProperty { thisRef, property ->

        synchronized(property) {
            if (instance == null) {
                instance = ArgumentWrapper(property.name, T::class.java, type) {
                    RequiredArgumentBuilder.argument(property.name, type)
                }
            }
        }

        instance!!
    }
}