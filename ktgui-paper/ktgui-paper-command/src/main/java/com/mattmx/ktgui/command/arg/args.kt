package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.registry.RegistryKey
import kotlin.properties.ReadOnlyProperty

fun <T> custom(argumentType: ArgumentType<T>) = delegate(argumentType)

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

fun <T : Any> resource(registryKey: RegistryKey<T>) = delegate(ArgumentTypes.resource(registryKey))
fun <T : Any> resourceKey(registryKey: RegistryKey<T>) = delegate(ArgumentTypes.resourceKey(registryKey))

fun boolean() = delegate(BoolArgumentType.bool())

fun string() = delegate(StringArgumentType.string())
fun word() = delegate(StringArgumentType.word())
fun greedyString() = delegate(StringArgumentType.greedyString())

fun int(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE) = delegate(IntegerArgumentType.integer(min, max))
fun long(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) = delegate(LongArgumentType.longArg(min, max))
fun float(min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE) = delegate(FloatArgumentType.floatArg(min, max))
fun double(min: Double = Double.MIN_VALUE, max: Double = Double.MAX_VALUE) =
    delegate(DoubleArgumentType.doubleArg(min, max))

fun <T, A : ArgumentBuilder<CommandSourceStack, A>> delegate(type: ArgumentType<T>): ReadOnlyProperty<Any?, RequiredArgumentBuilder<CommandSourceStack, A>> {
    var instance: RequiredArgumentBuilder<CommandSourceStack, A>? = null

    return ReadOnlyProperty { thisRef, property ->
        if (instance == null) {
            instance = Commands.argument(property.name, type) as RequiredArgumentBuilder<CommandSourceStack, A>
        }
        instance!!
    }
}