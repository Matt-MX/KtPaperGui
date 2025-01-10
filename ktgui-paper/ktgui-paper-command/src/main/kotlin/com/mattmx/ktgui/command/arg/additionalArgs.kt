package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.arguments.StringArgumentType
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.registry.RegistryKey
import kotlin.properties.ReadOnlyProperty

val DEFAULT_INVALID_INPUT = { input: String -> "Invalid input '${input}'" }
fun stringChoice(
    vararg pairs: String,
    invalid: (String) -> String = DEFAULT_INVALID_INPUT
): ReadOnlyProperty<Any?, ArgumentWrapper<String>> {
    return mapped(pairs.associateWith { it }, invalid = invalid)
}

inline fun <reified T : Any> mapped(
    vararg pairs: Pair<String, T>,
    noinline invalid: (String) -> String = DEFAULT_INVALID_INPUT
): ReadOnlyProperty<Any?, ArgumentWrapper<T>> {
    return mapped(pairs.toMap(), invalid = invalid)
}

inline fun <reified T : Any> mapped(
    map: Map<String, T>,
    noinline invalid: (String) -> String = DEFAULT_INVALID_INPUT
) = custom(
    customArgument<T, String>(StringArgumentType.word()) {
        suggests { context, builder ->
            map.keys.forEach(builder::suggest)
            builder.buildFuture()
        }
        convert { map[it] ?: error(invalid(it)) }
    }
)

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

fun prettyBoolean() = mapped<Boolean>(mapOf("on" to true, "off" to false))
