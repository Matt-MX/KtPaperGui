package com.mattmx.ktgui.command.node

import com.mattmx.ktgui.argument.Argument

class ArgumentNode<T>(
    val argument: Argument<T>
) : CommandNode<ArgumentNode<T>>()