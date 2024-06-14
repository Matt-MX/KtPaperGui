package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.arg.impl.*
import kotlin.math.min

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

    fun takeOne(argId: String) {
        values[argId] = next() ?: return
    }

    fun takeUntilNot(argId: String, block: String.() -> Boolean) {
        var current = next()
        val list = arrayListOf<String>()

        if (current != null) {
            list.add(current)
        }

        while (current != null && block(current)) {
            current = next()
            if (current != null) {
                list.add(current)
            }
        }
        values[argId] = list.joinToString(" ")
    }

    fun takeRemaining(argId: String) {
        takeUntilNot(argId) { pointer < args.size }
    }
}

fun main() {
    val ping by flag()
    val option by optionArgument<String>()
    val t by optionArgument<Int>()

    val args = "msg MattMX foo bar --t 5 --ping --option 'hello world'".split(" ")
    val processor = ArgumentProcessor(args)

    processor.permittedFlags.add(ping)

    processor.takeOne("username")
    processor.takeRemaining("msg")

    println(processor.values)
}