package com.mattmx.ktgui.commands.declarative.arg.consumer

class VariableArgumentConsumer(
    val to: Int
) : ArgumentConsumer {
    override fun consume(args: List<String>): List<String> {
        return args.subList(0, to)
    }
}