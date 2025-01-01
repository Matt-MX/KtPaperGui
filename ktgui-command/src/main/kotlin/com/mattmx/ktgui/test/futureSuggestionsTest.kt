package com.mattmx.ktgui.test

import com.mattmx.ktgui.command.CommandInvocation
import com.mattmx.ktgui.suggestions.FutureSuggestionsBuilder
import kotlin.concurrent.thread

fun main() {
    val invocation = CommandInvocation(
        "MattMX",
        "msg Ma".split(" ").toTypedArray(),
        "msg"
    )

    val future = FutureSuggestionsBuilder(invocation)

    getLazySuggestions(future)

    future.future.thenAccept { suggestions ->
        println(suggestions)
    }

    while (!future.future.isDone) {

    }
}

fun getLazySuggestions(future: FutureSuggestionsBuilder<*>) = thread {
    future.add("MattMX").add("GabbySimon")

    Thread.sleep(1000)

    future.add("test").filterLastArgStartsWith().complete()
}
