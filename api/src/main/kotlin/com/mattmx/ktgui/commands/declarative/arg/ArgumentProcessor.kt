package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.impl.*

class ArgumentProcessor(
    val command: DeclarativeCommandBuilder,
    val args: List<String>
) {
    var pointer = 0
    // Should be added to the command context
    val optionsAndFlags = hashMapOf<String, String>()

    fun peek(i: Int) = args.getOrNull(pointer + i)
    fun current() = peek(0)
    fun next(): String? {
        // Check if it is a flag
        pointer++
        while (current().let { it != null && command.optionsSyntax.match(it) }) {
            val optionOrPointerId = command.optionsSyntax.removePrefixFrom(current()!!)

            if (command.permittedFlags.any { it.chatName() == optionOrPointerId }) {
                optionsAndFlags[optionOrPointerId] = true.toString()
                pointer++
            } else if (command.permittedOptions.any { it.name() == optionOrPointerId }) {

                // TODO this should read the argument type args (does that make sense?)
                // e.g '--test "hello world"' -> hello world

                val value = peek(1) ?: continue
                optionsAndFlags[optionOrPointerId] = value
                pointer += 2
            } else break
        }

        return current()
    }

    fun reset() {
        this.pointer = 0
        this.optionsAndFlags.clear()
    }
}