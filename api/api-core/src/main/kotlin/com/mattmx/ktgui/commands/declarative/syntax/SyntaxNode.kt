package com.mattmx.ktgui.commands.declarative.syntax

abstract class SyntaxNode {

    abstract fun kind(): SyntaxKind

    open fun children(): List<SyntaxNode> {
        val children = arrayListOf<SyntaxNode>()
        // default impl of children, you should override this.
        for (member in this.javaClass.declaredFields) {
            if (member.type.isAssignableFrom(SyntaxNode::class.java)) {
                member.isAccessible = true
                val value = member.get(this)
                value?.let { children.add(value as SyntaxNode) }
            } else if (member.type.isAssignableFrom(Iterable::class.java)) {
                try {
                    member.isAccessible = true
                    val values = member.get(this) as Iterable<SyntaxNode>
                    children.addAll(values)
                } catch (_: ClassCastException) {
                }
            }
        }
        return children
    }

}