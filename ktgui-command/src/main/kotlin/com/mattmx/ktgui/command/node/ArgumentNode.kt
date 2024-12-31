package com.mattmx.ktgui.command.node

import com.mattmx.ktgui.argument.Argument

abstract class ArgumentNode<T> : CommandNode<ArgumentNode<T>>() {
    abstract val argument: Argument<T>
}