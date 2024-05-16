package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.consumer.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext

class StringArgument(
    name: String,
    type: String,
    consumer: ArgumentConsumer
) : Argument<String>(name, type, consumer) {
    var min: Int = 0
    var max: Int = Int.MAX_VALUE
    lateinit var regex: Regex
    val allowed = arrayListOf<Regex>()
    val disallow = arrayListOf<Regex>()

    override fun validate(stringValue: String?): Boolean {
        stringValue ?: return false

        val range = (min..max)

        val matchesRange = stringValue.length in range
        val matchesAllowed = allowed.any { it.matches(stringValue) }
        val meetsDisallowed = disallow.none { it.matches(stringValue) }

        return matchesRange && matchesAllowed && meetsDisallowed
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        stringValue: String?
    ): String? {
        return stringValue
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