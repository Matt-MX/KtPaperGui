package com.mattmx.ktgui.commands.declarative.arg.impl

data class OptionSyntax(
    val prefix: String = "--"
) {
    fun regex() = "$prefix.+".toRegex()

    fun match(str: String) = str.matches(regex())

    fun removePrefixFrom(str: String) = str.replaceFirst(prefix, "")
}