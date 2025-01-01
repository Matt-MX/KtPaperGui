package com.mattmx.ktgui.test

import com.mattmx.ktgui.command.CommandInvocation
import com.mattmx.ktgui.suggestions.SuggestionsBuilder

fun main() {
    val builder = SuggestionsBuilder()
        .add("MattMX")
        .add("Test", "Hello")
        .filterLastArgStartsWith()

    val invocation = CommandInvocation(
        "MattMX",
        "msg Ma".split(" ").toTypedArray(),
        "msg"
    )

    println(builder.getSuggestions(invocation))
}