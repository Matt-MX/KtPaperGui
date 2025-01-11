package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import kotlin.properties.ReadOnlyProperty

inline fun <reified T : Any> custom(argumentType: ArgumentType<T>) = delegate(argumentType)

fun boolean() = delegate(BoolArgumentType.bool())
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