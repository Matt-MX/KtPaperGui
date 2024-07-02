package com.mattmx.ktgui.commands.declarative.arg.consumers

import com.mattmx.ktgui.commands.declarative.arg.ArgumentProcessor

fun interface ArgumentConsumer {

    fun consume(processor: ArgumentProcessor): Result

    class Result(
        val stringValue: String?,
        val consumed: List<Int>
    ) {
        fun isEmpty() = stringValue == null

        companion object {
            private val EMPTY = Result(null, emptyList())
            fun empty() = EMPTY
        }

    }

    companion object {
        private val NONE = ArgumentConsumer { _ -> Result.empty() }
        private val SINGLE = ArgumentConsumer { processor -> Result(processor.next(), listOf(processor.pointer)) }

        @JvmStatic
        fun none() = NONE
        @JvmStatic
        fun single() = SINGLE

        @JvmStatic
        infix fun untilFalse(predicate: (ArgumentProcessor, String) -> Boolean) = ArgumentConsumer { processor ->
            var current = processor.next()
            val startIndex = processor.pointer
            var fullString = current ?: ""

            while (current != null) {
                current = processor.next()

                if (current == null) {
                    return@ArgumentConsumer Result.empty()
                }

                fullString += " $current"

                if (!predicate(processor, fullString)) {
                    return@ArgumentConsumer Result(fullString, (startIndex..processor.pointer).toList())
                }
            }
            Result.empty()
        }

        @JvmStatic
        infix fun until(predicate: (ArgumentProcessor, String) -> Boolean) = untilFalse { p, s -> !predicate(p, s) }

        @JvmStatic
        infix fun untilFalsePartial(predicate: (ArgumentProcessor, String) -> Boolean) = ArgumentConsumer { processor ->
            var current = processor.next()
            val startIndex = processor.pointer
            var fullString = current ?: ""

            while (current != null) {
                current = processor.next()

                if (current == null) {
                    return@ArgumentConsumer Result(fullString, (startIndex..processor.pointer).toList())
                }

                fullString += " $current"

                if (!predicate(processor, current)) {
                    return@ArgumentConsumer Result(fullString, (startIndex..processor.pointer).toList())
                }
            }
            Result(fullString, (startIndex..processor.pointer).toList())
        }

        @JvmStatic
        infix fun untilPartial(predicate: (ArgumentProcessor, String) -> Boolean) = untilFalsePartial { p, s -> !predicate(p, s) }

        @JvmStatic
        fun remaining() = untilPartial { processor, _ ->
            processor.pointer >= processor.args.size
        }

        @JvmStatic
        infix fun variable(amount: Int): ArgumentConsumer {
            var i = amount
            return untilFalse { _, _ -> i-- == 0 }
        }
    }
}