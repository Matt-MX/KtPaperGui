package com.mattmx.ktgui.commands.declarative

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class CommandContext<S : CommandSender>(
    val sender: S,
    val args: Array<String>,
) {
    private lateinit var map: Map<String, Any?>

    fun ref(name: String) = map[name]

    operator fun <T, V> ReadOnlyProperty<T, V>.getValue(
        thisRef: T,
        property: KProperty<*>
    ) : V {
        return map[property.name] as V
    }

    fun <T, V> getValue(argument: Argument<S, T, V>) : V? {
        return if (::map.isInitialized) map[argument.id] as V? else null
    }

    fun clone() = CommandContext(sender, args)

    fun withValues(map: List<Pair<String, Any?>>) = apply {
        this.map = map.toMap()
    }
}