package com.mattmx.ktgui.argument

import com.mattmx.ktgui.command.CommandInvocation
import com.mattmx.ktgui.command.node.ArgumentNode

abstract class Argument<T>(
    var name: String
) {

    abstract fun parse(invocation: CommandInvocation<*>, value: String) : Result<T>

    open fun suggests(invocation: CommandInvocation<*>, value: String) : Collection<String> {
        return emptyList()
    }

    operator fun unaryPlus() = ArgumentNode(this)
}