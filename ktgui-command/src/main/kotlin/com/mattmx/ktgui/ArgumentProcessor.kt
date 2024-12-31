package com.mattmx.ktgui

import com.mattmx.ktgui.argument.Argument
import com.mattmx.ktgui.argument.ArgumentContext
import com.mattmx.ktgui.command.CommandInvocation
import com.mattmx.ktgui.command.DeclarativeCommand
import com.mattmx.ktgui.command.node.ArgumentNode
import com.mattmx.ktgui.command.node.ChainNode
import com.mattmx.ktgui.command.node.CommandNode
import com.mattmx.ktgui.command.node.LiteralNode

class ArgumentProcessor(
    val command: CommandNode<*>,
    val invocation: CommandInvocation<*>
) {
    val argumentsMap = mutableMapOf<Argument<*>, ArgumentContext<*>>()
    var node: CommandNode<*>? = null
    var lastVisitedNode: CommandNode<*>? = null
    private val args = invocation.args.toList()
    private var start = 0
    private var end = 1

    fun process() {
        while (!isComplete()) {
            val substr = args.subList(start, end).joinToString(" ")
            var matchingChild: CommandNode<*>? = null

            val childIterator = currentNode().children().iterator()
            while (childIterator.hasNext() && matchingChild == null) {
                when (val child = childIterator.next()) {
                    is ArgumentNode<*> -> {
                        val result = child.argument.parse(invocation, substr)

                        if (result.isSuccess) {
                            argumentsMap[child.argument] = ArgumentContext(
                                result.getOrNull(),
                                substr
                            )
                            matchingChild = child
                        }
                    }

                    is LiteralNode -> {
                        if (child.name == substr) {
                            matchingChild = child
                        }
                    }
                }
            }

            if (matchingChild != null) {
                start = end
                end++

                if (isComplete()) {
                    this.node = matchingChild
                } else {
                    this.lastVisitedNode = matchingChild
                }
            } else {
                end++
            }
        }
    }

    fun isComplete() = end > args.size

    fun currentNode() = lastVisitedNode ?: command

}