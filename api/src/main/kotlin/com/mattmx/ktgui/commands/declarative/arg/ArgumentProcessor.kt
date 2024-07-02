package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.impl.*

class ArgumentProcessor(
    val command: DeclarativeCommandBuilder,
    val args: List<String>
) {
    var pointer = -1
    var optionsAndFlagsValues = hashMapOf<String, Any>()

    fun peek(i: Int) = args.getOrNull(pointer + i)
    fun current() = peek(0)
    fun next(): String? {
        // Check if it is a flag
        pointer++
        while (current().let { it != null && command.optionsSyntax.match(it) }) {
            val optionOrPointerId = command.optionsSyntax.removePrefixFrom(current()!!)

            if (command.permittedFlags.any { it.chatName() == optionOrPointerId }) {
                optionsAndFlagsValues[optionOrPointerId] = true
                pointer++
            } else if (command.permittedOptions.any { it.chatName() == optionOrPointerId }) {

                // TODO this should read the argument type args (does that make sense?)
                // e.g '--test "hello world"' -> hello world

                val value = peek(1) ?: continue
                optionsAndFlagsValues[optionOrPointerId] = value
                pointer += 2
            } else break
        }

        return current()
    }

    fun reset() {
        this.pointer = -1
        this.optionsAndFlagsValues.clear()
    }

    fun clone() = ArgumentProcessor(command, args).apply {
        this.optionsAndFlagsValues = this@ArgumentProcessor.optionsAndFlagsValues
        this.pointer = this@ArgumentProcessor.pointer
    }

    fun done() = pointer >= args.size - 1
}