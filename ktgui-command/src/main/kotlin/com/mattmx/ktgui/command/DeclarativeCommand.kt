package com.mattmx.ktgui.command

import com.mattmx.ktgui.ArgumentProcessor
import com.mattmx.ktgui.command.node.LiteralNode

class DeclarativeCommand(
    name: String
) : LiteralNode(name) {

    fun run(invocation: CommandInvocation<*>) {
        val processor = ArgumentProcessor(this, invocation)

        processor.process()

        processor.node?.invokeRunBlock(processor, invocation)
    }

}