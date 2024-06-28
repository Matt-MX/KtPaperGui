package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.arg.consumers.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.impl.*

class ArgumentProcessor(
    val args: List<String>
) {
    var pointer = 0
    // Should be added to the command context
    val values = hashMapOf<String, String>()

    // Should be in the [DeclarativeCommandBuilder] class
    val permittedFlags = arrayListOf<FlagArgument>()
    val options = OptionSyntax()

    fun peek(i: Int) = args.getOrNull(pointer + i)
    fun current() = peek(0)
    fun next(): String? {
        // Check if it is a flag
        pointer++
        while (current().let { it != null && options.match(it) }) {
            val optionOrPointerId = options.removePrefixFrom(current()!!)

            // TODO check if expected id is expected and if it is a flag or option
            if (permittedFlags.any { it.chatName() == optionOrPointerId }) {
                values[optionOrPointerId] = true.toString()
            } else {
                // TODO this should read the argument type args (does that make sense?)
                // e.g '--test "hello world"' -> hello world
                val value = peek(1) ?: continue
                values[optionOrPointerId] = value
                pointer++
            }

            pointer++
        }

        return current()
    }
}

fun main() {
    val ping by flag()
    val option by optionArgument<String>()
    val t by optionArgument<Int>()

    val args = "msg MattMX foo bar --t 5 --ping --option test".split(" ")
    val processor = ArgumentProcessor(args)

    processor.permittedFlags.add(ping)

    // We should abstract this using the `ArgumentConsumer` interface
    println("username" + ArgumentConsumer.single().consume(processor))
    println("msg" + ArgumentConsumer.remaining().consume(processor))

    println(processor.values)

    val regexArgumentConsumer = ArgumentConsumer.until { _, s -> s.matches("\\{.+}".toRegex()) }
    val regexProcessor = ArgumentProcessor("msg {hello world}".split(" "))
    println(regexArgumentConsumer.consume(regexProcessor))
}