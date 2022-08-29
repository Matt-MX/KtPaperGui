package com.mattmx.ktguis.utils

import com.google.gson.JsonParser
import com.mattmx.ktguis.KotlinBukkitGui
import java.net.URL

class GitUpdateChecker(
    val url: String,
    val current: String,
    val then: ((Boolean, String) -> Unit)? = null
) {
    init {
        val str = URL(url).readText()
        val json = JsonParser.parseString(str).asJsonObject
        val latestRelease = json.get("tag_name").asString
        then?.invoke(current != latestRelease, latestRelease)
    }
}