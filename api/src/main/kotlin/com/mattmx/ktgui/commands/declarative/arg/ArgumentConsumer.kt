package com.mattmx.ktgui.commands.declarative.arg

fun interface ArgumentConsumer {

    fun consume(processor: ArgumentProcessor): Result

    class Result(
        val args: List<String>?
    ) {
        constructor(single: String?) : this(single?.let { listOf(it) })

        val argsAsStringValue = args?.joinToString(" ")

        fun isEmpty() = args.isNullOrEmpty()

        override fun toString() = "Result($args)"

        companion object {
            private val EMPTY = Result(emptyList())
            fun empty() = EMPTY
        }

    }

    companion object {
        private val NONE = ArgumentConsumer { _ -> Result.empty() }
        private val SINGLE = ArgumentConsumer { processor -> ArgumentConsumer.Result(processor.next()) }

        @JvmStatic
        fun none() = NONE
        @JvmStatic
        fun single() = SINGLE

        @JvmStatic
        infix fun untilFalse(predicate: (ArgumentProcessor, List<String>) -> Boolean) = ArgumentConsumer { processor ->
            var current: String? = ""
            val consumed = arrayListOf<String>()

            while (current != null) {
                current = processor.next()

                if (current == null) {
                    return@ArgumentConsumer Result.empty()
                }

                consumed += current

                if (!predicate(processor, consumed)) {
                    return@ArgumentConsumer Result(consumed)
                }
            }
            Result.empty()
        }

        @JvmStatic
        infix fun until(predicate: (ArgumentProcessor, List<String>) -> Boolean) = untilFalse { p, s -> !predicate(p, s) }

        @JvmStatic
        infix fun untilFalsePartial(predicate: (ArgumentProcessor, String) -> Boolean) = ArgumentConsumer { processor ->
            var current: String? = null
            val consumed = arrayListOf<String>()

            while (current != null) {
                current = processor.next()

                if (current == null) {
                    return@ArgumentConsumer Result(consumed)
                }

                consumed += current

                if (!predicate(processor, current)) {
                    return@ArgumentConsumer Result(consumed)
                }
            }
            Result(consumed)
        }

        @JvmStatic
        infix fun untilPartial(predicate: (ArgumentProcessor, String) -> Boolean) = untilFalsePartial { p, s -> !predicate(p, s) }

        @JvmStatic
        fun remaining() = untilFalse { processor, _ ->
            !processor.done()
        }

        @JvmStatic
        infix fun variable(amount: Int): ArgumentConsumer {
            var i = amount
            return untilFalse { _, _ -> i-- == 0 }
        }
    }
}