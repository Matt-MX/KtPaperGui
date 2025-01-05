package com.mattmx.ktgui

import com.google.gson.JsonParser
import com.mattmx.ktgui.argument.stringArgument
import com.mattmx.ktgui.command.CommandInvocation
import com.mattmx.ktgui.command.command

fun main() {
    val invocation = CommandInvocation(
        "MattMX",
        "{\"test\": \"Hello, World\"} someStringValue 933 greedy argument test".split(" ").toTypedArray(),
        ""
    )

    val test by stringArgument()
    val command = command("parse-json") {
        sub(test).runs<String> {
            println("$sender -> ${test()}")
        }
    }

    val processor = ArgumentProcessor(command, invocation)

    val jsonArgumentConsumer = ArgumentConsumer.untilTrue { context ->
        runCatching {
            JsonParser.parseString(context.currentString())
        }.getOrNull() != null
    }

    val wordConsumer = ArgumentConsumer.single()
    val intConsumer = ArgumentConsumer.single()
    val greedyConsumer = ArgumentConsumer.untilTrue { context ->
        context.isComplete()
    }

    val json = jsonArgumentConsumer.consume(processor)
    processor.start = processor.end
    processor.end++
    val word = wordConsumer.consume(processor)
    processor.start = processor.end
    processor.end++
    val int = intConsumer.consume(processor)
    processor.start = processor.end
    processor.end++
    val greedy = greedyConsumer.consume(processor)

    println("$json $word $int $greedy")
}