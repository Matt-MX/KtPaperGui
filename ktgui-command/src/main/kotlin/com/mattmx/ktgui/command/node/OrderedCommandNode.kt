package com.mattmx.ktgui.command.node

class OrderedCommandNode(
    val args: MutableList<CommandNode<*>> = mutableListOf()
) {

    operator fun div(other: CommandNode<*>): OrderedCommandNode {
        this.args.add(other)
        return this
    }

    fun build() : Pair<CommandNode<*>, CommandNode<*>> {
        val built = args.reduce { a, b ->
            a.addChild(b)
            b
        }

        return args.first() to built
    }

}