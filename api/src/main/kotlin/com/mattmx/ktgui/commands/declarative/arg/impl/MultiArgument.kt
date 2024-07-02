package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.arg.Argument

class MultiArgument(
    name: String,
    vararg val args: Argument<*>
) : Argument<Argument<*>>(name, "any") {
}