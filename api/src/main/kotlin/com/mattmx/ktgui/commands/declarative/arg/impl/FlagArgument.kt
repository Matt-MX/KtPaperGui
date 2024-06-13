package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer

class FlagArgument(
    name: String
) : Argument<String>(name, "boolean", SingleArgumentConsumer()) {

    fun chatName() = name().replace("_", "-")

}