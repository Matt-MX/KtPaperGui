package com.mattmx.ktgui.commands.declarative.arg.consumers

import com.mattmx.ktgui.commands.declarative.arg.ArgumentProcessor

fun interface ArgumentConsumer {
    fun consume(processor: ArgumentProcessor): String?

    companion object {
        fun single() = ArgumentConsumer { processor -> processor.next() }

        infix fun untilFalse(predicate: (ArgumentProcessor, String) -> Boolean) = ArgumentConsumer { processor ->
            var current = processor.next()
            var fullString = current ?: ""

            while (current != null) {
                current = processor.next()

                if (current == null) {
                    return@ArgumentConsumer null
                }

                fullString += " $current"

                if (!predicate(processor, fullString)) {
                    return@ArgumentConsumer fullString
                }
            }
            null
        }

        infix fun until(predicate: (ArgumentProcessor, String) -> Boolean) = untilFalse { p, s -> !predicate(p, s) }

        infix fun untilFalsePartial(predicate: (ArgumentProcessor, String) -> Boolean) = ArgumentConsumer { processor ->
            var current = processor.next()
            var fullString = current ?: ""

            while (current != null) {
                current = processor.next()

                if (current == null) {
                    return@ArgumentConsumer null
                }

                fullString += " $current"

                if (!predicate(processor, current)) {
                    return@ArgumentConsumer fullString
                }
            }
            null
        }

        infix fun untilPartial(predicate: (ArgumentProcessor, String) -> Boolean) = untilFalsePartial { p, s -> !predicate(p, s) }

        fun remaining() = untilFalse { processor, _ -> processor.pointer < processor.args.size }

        infix fun variable(amount: Int): ArgumentConsumer {
            var i = amount
            return untilFalse { _, _ -> i-- == 0 }
        }
    }
}