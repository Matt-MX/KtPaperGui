package com.mattmx.ktgui.utils

import com.google.gson.JsonElement
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.event.HoverEvent.ShowEntity
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.regex.Matcher
import java.util.regex.Pattern

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
private val pattern: Pattern = Pattern.compile("&#[a-fA-F0-9]{6}")
fun String.legacyColor(): String {
    var string = this
    var match: Matcher = pattern.matcher(string)
    while (match.find()) {
        val color: String = string.substring(match.start() + 1, match.end())
        val color1: String = string.substring(match.start(), match.end())
        string = string.replace(color1, "${ChatColor.of(color)}")
        match = pattern.matcher(string)
    }
    return ChatColor.translateAlternateColorCodes('&', string)
}
operator fun Component.plus(component: Component) = this.append(component)
infix fun Component.clickEvent(event: ClickEvent) = clickEvent(event)
infix fun <T> Component.hoverEvent(event: HoverEvent<T>) = hoverEvent(event)
operator fun String.not() = component()
