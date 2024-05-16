package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.arg.consumer.ArgumentConsumer

class MultiArgument(
    name: String,
    vararg val args: Argument<*>
) : Argument<Argument<*>>(name, "any", MultiArgConsumer(*args)) {
    class MultiArgConsumer(vararg val args: Argument<*>) : ArgumentConsumer {
        override fun consume(args: List<String>): List<String> {
            val all = this.args.map { it.consumer.consume(args).size }

            val largest = all.maxOf { it }

            return args.subList(0, largest)
        }

    }
}