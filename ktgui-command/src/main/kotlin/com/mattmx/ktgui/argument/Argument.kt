package com.mattmx.ktgui.argument

import com.mattmx.ktgui.ArgumentConsumer
import com.mattmx.ktgui.command.CommandInvocation
import com.mattmx.ktgui.command.node.ArgumentNode

abstract class Argument<T>(
    var name: String
) : ArgumentNode<T>() {
    abstract var consumer: ArgumentConsumer
    override val argument: Argument<T>
        get() = this

    abstract fun parse(invocation: CommandInvocation<*>, value: String) : Result<T>

    open fun suggests(invocation: CommandInvocation<*>, value: String) : Collection<String> {
        return emptyList()
    }
}