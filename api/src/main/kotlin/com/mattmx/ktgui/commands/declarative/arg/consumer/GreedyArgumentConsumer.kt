package com.mattmx.ktgui.commands.declarative.arg.consumer

class GreedyArgumentConsumer : ArgumentConsumer {
    override fun consume(args: List<String>): List<String> {
        return args
    }
}