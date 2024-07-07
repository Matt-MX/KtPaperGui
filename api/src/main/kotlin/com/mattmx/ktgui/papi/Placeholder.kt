package com.mattmx.ktgui.papi

import com.mattmx.ktgui.commands.declarative.ChainCommandBuilder
import java.util.*

class Placeholder(
    val match: ChainCommandBuilder,
    val supplier: (PlaceholderParseContext) -> Any?
) {
    var description = Optional.empty<String>()
        private set
    var priority = 0

    fun parse(context: PlaceholderParseContext) = supplier.invoke(context)

    infix fun description(desc: String?) = apply {
        this.description = Optional.ofNullable(desc)
    }

    override fun toString() =
        "%${match.name}_${
            match.arguments.joinToString("_") { arg ->
                "<${arg.name()}${if (arg.isOptional()) "?" else ""}:${arg.type()}>"
            }
        }%${if (description.isPresent) " - ${description.get()}" else ""}"
}