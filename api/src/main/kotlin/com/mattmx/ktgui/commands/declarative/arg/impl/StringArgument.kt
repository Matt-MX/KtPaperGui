package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class StringArgument(
    name: String,
    type: String
) : Argument<String>(name, type) {
    var min: Int = 0
    var max: Int = Int.MAX_VALUE
    val allowed = arrayListOf<Regex>()
    val disallow = arrayListOf<Regex>()

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): String? {
        stringValue ?: return null

        val range = (min..max)

        val matchesRange = stringValue.length in range
        val matchesAllowed = allowed.all { it.matches(stringValue) }
        val meetsDisallowed = disallow.none { it.matches(stringValue) }

        val all = matchesRange && matchesAllowed && meetsDisallowed

        return if (all) stringValue else null
    }

    infix fun greedy(isGreedy: Boolean) = apply {
        consumes(if (isGreedy) ArgumentConsumer.remaining() else ArgumentConsumer.single())
    }

    infix fun allow(regex: List<Regex>) = apply {
        this.allowed += regex
    }

    infix fun disallow(regex: List<Regex>) = apply {
        this.disallow += regex
    }

    infix fun allow(regex: Regex) = apply {
        this.allowed += regex
    }

    infix fun disallow(regex: Regex) = apply {
        this.disallow += regex
    }

    infix fun matches(regex: Regex) = allow(regex)

    infix fun range(range: IntRange) = apply {
        this.min = range.first
        this.max = range.last
    }

    infix fun min(min: Int) = apply {
        this.min = min
    }

    infix fun max(max: Int) = apply {
        this.max = max
    }

}