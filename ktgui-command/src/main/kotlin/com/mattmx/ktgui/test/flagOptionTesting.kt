package com.mattmx.ktgui.test

import com.mattmx.ktgui.ArgumentConsumer
import com.mattmx.ktgui.ArgumentProcessor
import com.mattmx.ktgui.Flag
import com.mattmx.ktgui.Option
import com.mattmx.ktgui.argument.intArgument
import com.mattmx.ktgui.argument.stringArgument
import com.mattmx.ktgui.command.CommandInvocation

fun main() {
    val username by stringArgument()
    val page by intArgument()
    val pageOption = Option(page)
    val flag = Flag("flag")

    val cmd = command("history") {
        sub(username) {
            withOption(pageOption)
            flags.add(flag)
        }
    }

    val invocation = CommandInvocation(
        "MattMX",
        "MattMX --page 10 -flag".split(" ").toTypedArray(),
        "test"
    )

    repeat(2) {
        timeAndLog("processing") {
            val processor = ArgumentProcessor(cmd, invocation)

            val usernameResult = ArgumentConsumer.single().consume(processor)
            processor.next()
            val pageResult = ArgumentConsumer.option(pageOption).consume(processor)
            processor.next()
            val flagPresent = ArgumentConsumer.flag(flag).consume(processor)

            println("$usernameResult $pageResult $flagPresent")
        }
    }
}