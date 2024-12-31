package com.mattmx.ktgui

class Flag(
    var name: String,
    var prefix: String = "-"
) {
    val regex: Regex
        get() = "$prefix$name".toRegex()
}