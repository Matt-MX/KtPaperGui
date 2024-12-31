package com.mattmx.ktgui.command.node

import com.mattmx.ktgui.ArgumentProcessor
import com.mattmx.ktgui.command.CommandInvocation
import com.mattmx.ktgui.command.context.RunnableCommandContext

open class CommandNode<T : CommandNode<T>> : ChildNodeHolder() {
    val runs = mutableMapOf<Class<*>, (RunnableCommandContext<*>) -> Unit>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> runs(noinline block: RunnableCommandContext<T>.() -> Unit) = apply {
        this.runs[T::class.java] = block as (RunnableCommandContext<*>) -> Unit
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> executes(noinline block: (RunnableCommandContext<T>) -> Unit) = apply {
        this.runs[T::class.java] = block as (RunnableCommandContext<*>) -> Unit
    }

    @Suppress("UNCHECKED_CAST")
    operator fun invoke(block: T.() -> Unit) = apply {
        block(this as T)
    }

    operator fun CommandNode<*>.div(other: CommandNode<*>) : OrderedCommandNode {
        return OrderedCommandNode(mutableListOf(this, other))
    }

    operator fun CommandNode<*>.div(other: String) : OrderedCommandNode {
        return OrderedCommandNode(mutableListOf(this, LiteralNode(other)))
    }

    operator fun String.div(other: String): OrderedCommandNode {
        return OrderedCommandNode(mutableListOf(LiteralNode(this), LiteralNode(other)))
    }

    operator fun <T : CommandNode<T>> String.div(other: T): OrderedCommandNode {
        return OrderedCommandNode(mutableListOf(LiteralNode(this), other))
    }

    fun sub(node: CommandNode<*>) = node.also { addChild(it) }

    fun sub(node: String) = LiteralNode(node).also { addChild(it) }

    fun sub(orderedCommandNode: OrderedCommandNode): CommandNode<*> {
        val (root, end) = orderedCommandNode.build()

        if (root is LiteralNode) {
            val duplicate = children()
                .filterIsInstance<LiteralNode>()
                .firstOrNull { literal -> literal.name == root.name }

            if (duplicate != null) {

                for (child in root.children().filterIsInstance<CommandNode<*>>()) {
                    duplicate.sub(child)
                }

                return end
            }
        }

        addChild(root)

        return end
    }

    fun <T : CommandNode<T>> sub(orderedCommandNode: OrderedCommandNode, block: CommandNode<*>.() -> Unit) = sub(orderedCommandNode).apply(block)

    fun <T : CommandNode<T>> sub(node: T, block: T.() -> Unit) = node.apply(block).also { addChild(it) }

    fun sub(literal: String, block: LiteralNode.() -> Unit) = LiteralNode(literal).apply(block).also { addChild(it) }

    fun invokeRunBlock(processor: ArgumentProcessor, invocation: CommandInvocation<*>) {
        if (invocation.sender == null) {
            error("CommandInvocation's source cannot be null!")
        }

        val callback = runs[invocation.sender::class.java]

        val context = RunnableCommandContext(invocation.sender, invocation.args, processor.argumentsMap)

        callback?.invoke(context)
    }
}