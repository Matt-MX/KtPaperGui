package com.mattmx.ktgui.commands.declarative.arg.consumer

class SingleArgumentConsumer : ArgumentConsumer {
    override fun consume(args: List<String>): List<String> {
        return args.firstOrNull()?.let { listOf(it) } ?: emptyList()
    }
}