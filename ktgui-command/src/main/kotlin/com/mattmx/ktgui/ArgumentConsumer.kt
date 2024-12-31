package com.mattmx.ktgui

fun interface ArgumentConsumer {

    fun consume(context: ArgumentProcessor) : Result<Any>

    companion object {
        private val SINGLE = ArgumentConsumer { context ->
            Result.success(context.currentString())
        }

        fun single() = SINGLE

        fun whileTrue(statement: (ArgumentProcessor) -> Boolean) = ArgumentConsumer { context ->
            while (statement(context)) {
                context.end++
            }

            val currentString = context.currentString()

            if (currentString.isEmpty()) {
                return@ArgumentConsumer Result.failure(RuntimeException())
            }

            Result.success(currentString)
        }

        fun untilTrue(statement: (ArgumentProcessor) -> Boolean) =
            whileTrue { context -> !statement(context) }

        fun <T> option(option: Option<T>): ArgumentConsumer {
            return ArgumentConsumer { context ->
                val matchStart = context.peekStart(0).matches(option.regex)
                context.next()

                if (!matchStart) {
                    return@ArgumentConsumer Result.failure(RuntimeException())
                }

                option.argument.consumer.consume(context)
            }
        }

        fun flag(flag: Flag) : ArgumentConsumer {
            return ArgumentConsumer { context ->
                val matchFlag = context.peekStart(0).matches(flag.regex)

                if (matchFlag) {
                    Result.success(true)
                } else {
                    Result.success(false)
                }
            }
        }
    }

}