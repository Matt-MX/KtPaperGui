package com.mattmx.ktgui.utils

import com.google.gson.JsonParser
import java.lang.Exception
import java.net.URL

class GitUpdateChecker(
    val url: String,
    val current: String,
    val then: ((Boolean, String) -> Unit)? = null,
    val error: ((Exception) -> Unit)? = null
) {
    init {
        try {
            val str = URL(url).readText()
            val json = JsonParser.parseString(str).asJsonObject
            val latestRelease = json.get("tag_name").asString
            then?.invoke(current != latestRelease, latestRelease)
        } catch (e: Exception) {
            error?.invoke(e)
        }
    }
}