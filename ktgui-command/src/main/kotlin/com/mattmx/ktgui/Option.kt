package com.mattmx.ktgui

import com.mattmx.ktgui.argument.Argument

open class Option<T>(
    var argument: Argument<T>,
    var prefix: String = "--"
) {
    val regex: Regex
        get() = "$prefix${argument.name}".toRegex()

    infix fun prefix(prefix: String) {
        this.prefix = prefix
    }
}