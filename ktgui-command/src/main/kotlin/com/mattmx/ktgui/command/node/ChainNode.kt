package com.mattmx.ktgui.command.node

class ChainNode(
    val parts: MutableList<ChildNodeHolder>
) : CommandNode<ChainNode>() {
    fun compile(): ChildNodeHolder {
        val root = parts.reduceRight { a, b ->
            b.addChild(a)
            b
        }

        return root
    }
}