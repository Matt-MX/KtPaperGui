package com.mattmx.ktgui.utils

import com.google.gson.JsonElement
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.OfflinePlayer

var serializer = LegacyComponentSerializer.builder()
    .character('&')
    .hexCharacter('#')
    .hexColors()
    .build()

var gsonSerializer = GsonComponentSerializer.builder().build()
val legacySerializer = LegacyComponentSerializer.builder().build()

fun Component.json() = gsonSerializer.serialize(this)
fun Component.legacy() = legacySerializer.serialize(this)
fun String.jsonToComponent() = gsonSerializer.deserialize(this)
fun JsonElement.component() = gsonSerializer.deserializeFromTree(this)
fun String.component() = serializer.deserialize(this)
val String.component: Component
    get() = component()
fun String.legacyToComponent() = legacySerializer.deserialize(this)
fun String.placeholders(player: OfflinePlayer? = null): String {
    if (Dependencies.papi) {
        return PlaceholderAPI.setPlaceholders(player, this)
    }
    return this
}
fun Component.plus(component: Component) = this.append(component)
