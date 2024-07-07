package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.impl.*
import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext

class ArgumentProcessor(
    val command: DeclarativeCommandBuilder,
    val context: StorageCommandContext<*>?,
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

            val flag = command.permittedFlags.firstOrNull { it.chatName() == optionOrPointerId }

            if (flag != null) {
                val passed = if (flag.requiresCheck.isEmpty) true else flag.requiresCheck.isPresent

                if (passed) {
                    optionsAndFlagsValues[optionOrPointerId] = true
                }

                pointer++
            } else {
                val option = command.permittedOptions.firstOrNull {
                    it.chatName() == optionOrPointerId
                }

                if (option != null) {
                    val passed = if (option.requiresCheck.isEmpty) true
                    else context?.let { option.requiresCheck.get()(it) } == true

                    if (passed) {
                        val value = peek(1) ?: continue
                        optionsAndFlagsValues[optionOrPointerId] = value
                    }

                    pointer += 2
                } else break
            }
        }

        return current()
    }

    fun reset() {
        this.pointer = -1
        this.optionsAndFlagsValues.clear()
    }

    fun clone() = ArgumentProcessor(command, context, args).apply {
        this.optionsAndFlagsValues = this@ArgumentProcessor.optionsAndFlagsValues
        this.pointer = this@ArgumentProcessor.pointer
    }

    fun done() = pointer >= args.size - 1
}