package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.arguments.*
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.registry.RegistryKey
import kotlin.properties.ReadOnlyProperty

inline fun <reified T : Any> custom(argumentType: ArgumentType<T>) = delegate(argumentType)

inline fun <reified T : Any> mapped(vararg pairs: Pair<String, T>): ReadOnlyProperty<Any?, ArgumentWrapper<T>> {
    return mapped(pairs.toMap())
}
inline fun <reified T : Any> mapped(map: Map<String, T>) = custom(
    customArgument<T, String>(StringArgumentType.word()) {
        suggests { context, builder ->
            map.keys.forEach(builder::suggest)
            builder.buildFuture()
        }
        convert { map[it] ?: error("Invalid input!") }
    }
)

fun options(vararg option: ArgumentWrapper<*>) = delegate(OptionFlagArgumentType(option.toList()))

fun player() = delegate(ArgumentTypes.player())
fun players() = delegate(ArgumentTypes.players())
fun entity() = delegate(ArgumentTypes.entity())
fun entities() = delegate(ArgumentTypes.entities())
fun pos(center: Boolean = false) = delegate(ArgumentTypes.finePosition(center))
fun blockPos() = delegate(ArgumentTypes.blockPosition())
fun blockState() = delegate(ArgumentTypes.blockState())
fun itemStack() = delegate(ArgumentTypes.itemStack())
fun itemStackPredicate() = delegate(ArgumentTypes.itemPredicate())
fun namedColor() = delegate(ArgumentTypes.namedColor())
fun namespacedKey() = delegate(ArgumentTypes.namespacedKey())
fun component() = delegate(ArgumentTypes.component())
fun style() = delegate(ArgumentTypes.style())
fun signedMessage() = delegate(ArgumentTypes.signedMessage())
fun scoreBoardDisplaySlot() = delegate(ArgumentTypes.scoreboardDisplaySlot())
fun key() = delegate(ArgumentTypes.key())
fun intRange() = delegate(ArgumentTypes.integerRange())
fun doubleRange() = delegate(ArgumentTypes.doubleRange())
fun world() = delegate(ArgumentTypes.world())
fun gameMode() = delegate(ArgumentTypes.gameMode())
fun heightMap() = delegate(ArgumentTypes.heightMap())
fun uuid() = delegate(ArgumentTypes.uuid())
fun objectiveCriteria() = delegate(ArgumentTypes.objectiveCriteria())
fun lookAnchor() = delegate(ArgumentTypes.entityAnchor())
fun time(min: Int = 0) = delegate(ArgumentTypes.time(min))
fun templateMirror() = delegate(ArgumentTypes.templateMirror())
fun templateRotation() = delegate(ArgumentTypes.templateRotation())

inline fun <reified T : Any> resource(registryKey: RegistryKey<T>) = delegate(ArgumentTypes.resource(registryKey))
fun <T : Any> resourceKey(registryKey: RegistryKey<T>) = delegate(ArgumentTypes.resourceKey(registryKey))

fun boolean() = delegate(BoolArgumentType.bool())
fun prettyBoolean() = mapped<Boolean>(mapOf("on" to true, "off" to false))

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
                instance = ArgumentWrapper(property.name, T::class.java, type) { Commands.argument(property.name, type) }
            }
        }

        instance!!
    }
}