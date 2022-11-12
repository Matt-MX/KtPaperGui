package com.mattmx.ktgui.utils

import net.md_5.bungee.api.ChatColor
import java.util.regex.Matcher
import java.util.regex.Pattern

fun color(s: String, vararg placeholders: Pair<String, String>): String {
    var string = s
    placeholders.forEach {
        string = string.replace(it.first, it.second)
    }
    return color(string)
}

private val pattern: Pattern = Pattern.compile("&#[a-fA-F0-9]{6}")
fun color(s: String): String {
    var string = s
    var match: Matcher = pattern.matcher(string)
    while (match.find()) {
        val color: String = string.substring(match.start() + 1, match.end())
        val color1: String = string.substring(match.start(), match.end())
        string = string.replace(color1, "${ChatColor.of(color)}")
        match = pattern.matcher(string)
    }
    return ChatColor.translateAlternateColorCodes('&', string)
}