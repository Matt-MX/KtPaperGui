package com.mattmx.ktgui.command.node

open class ChildNodeHolder {
    private val children = mutableListOf<ChildNodeHolder>()

    fun addChild(child: ChildNodeHolder): Boolean {
        return children.add(child)
    }

    fun removeChild(child: ChildNodeHolder): Boolean {
        return children.remove(child)
    }

    fun children(): List<ChildNodeHolder> {
        return children.sortedBy { it.priority() }
    }

    open fun priority() = 0
}