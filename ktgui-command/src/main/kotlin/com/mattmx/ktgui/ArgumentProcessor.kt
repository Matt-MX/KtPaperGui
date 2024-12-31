package com.mattmx.ktgui

import com.mattmx.ktgui.argument.Argument
import com.mattmx.ktgui.argument.ArgumentContext
import com.mattmx.ktgui.command.CommandInvocation
import com.mattmx.ktgui.command.node.ArgumentNode
import com.mattmx.ktgui.command.node.CommandNode
import com.mattmx.ktgui.command.node.LiteralNode
import kotlin.math.max
import kotlin.math.min

class ArgumentProcessor(
    val command: CommandNode<*>,
    val invocation: CommandInvocation<*>
) {
    val argumentsMap = mutableMapOf<Argument<*>, ArgumentContext<*>>()
    var node: CommandNode<*>? = null
    var lastVisitedNode: CommandNode<*>? = null
    private val args = invocation.args.toList()
    var start = 0
    var end = 1

    fun process() {
        while (!isComplete()) {
            val substr = currentString()
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

    fun currentString(): String {
        return peekAdditional(0)
    }

    fun peek(i: Int) : String = args[end + i]
    fun peekStart(i: Int) : String = args[start + i]

    fun peekAdditional(i: Int) : String {
        return args.subList(start, min(args.size, end + i)).joinToString(" ")
    }

    fun peekOffset(a: Int, b: Int = args.size) : String {
        return args.subList(max(0, start + a), min(args.size, end + b)).joinToString(" ")
    }

    fun next() {
        start = end
        end++
    }

}