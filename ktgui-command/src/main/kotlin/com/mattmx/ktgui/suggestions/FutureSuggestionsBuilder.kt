package com.mattmx.ktgui.suggestions

import com.mattmx.ktgui.command.CommandInvocation
import java.util.concurrent.CompletableFuture

class FutureSuggestionsBuilder<T : FutureSuggestionsBuilder<T>>(
    val invocation: CommandInvocation<*>
) : SuggestionsBuilder<T>() {
    val future = CompletableFuture<List<String>>()

    fun complete() {
        future.complete(getSuggestions(invocation))
    }

}