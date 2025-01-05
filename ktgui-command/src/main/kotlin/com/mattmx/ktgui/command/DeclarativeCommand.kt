package com.mattmx.ktgui.command

import com.mattmx.ktgui.ArgumentProcessor
import com.mattmx.ktgui.command.node.LiteralNode

class DeclarativeCommand(
    name: String
) : LiteralNode(name) {

    fun process(invocation: CommandInvocation<*>) : ArgumentProcessor {
        val processor = ArgumentProcessor(this, invocation)
            .also(ArgumentProcessor::process)

        return processor
    }

    fun run(invocation: CommandInvocation<*>) {
        process(invocation).let { processor ->
            processor.node?.invokeRunBlock(processor, invocation)
        }
    }

}