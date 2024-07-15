package com.mattmx.ktgui.papi

import com.mattmx.ktgui.commands.declarative.ChainCommandBuilder
import com.mattmx.ktgui.utils.JavaCompatibility
import java.util.*

class Placeholder(
    val owner: PlaceholderExpansionWrapper,
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
        "%${owner.id}_${if (match.name == EMPTY_PLACEHOLDER) "" else "${match.name}_"}${
            match.arguments.joinToString("_") { arg ->
                "<${arg.name()}${if (arg.isOptional()) "?" else ""}:${arg.type()}>"
            }
        }%${if (description.isPresent) " - ${description.get()}" else ""}"

    @JavaCompatibility
    class Builder {
        var match = Optional.empty<ChainCommandBuilder>()
            private set
        var supplier = Optional.empty<(PlaceholderParseContext) -> Any?>()
            private set
        var description = Optional.empty<String>()
            private set
        var priority = 0
            private set

        infix fun matches(builder: ChainCommandBuilder) = apply {
            this.match = Optional.of(builder)
        }

        infix fun supplier(supplier: (PlaceholderParseContext) -> Any?) = apply {
            this.supplier = Optional.of(supplier)
        }

        infix fun description(description: String?) = apply {
            this.description = Optional.ofNullable(description)
        }

        infix fun priority(priority: Int) = apply {
            this.priority = priority
        }

        fun build(owner: PlaceholderExpansionWrapper) =
            Placeholder(owner, match.get(), supplier.get())
                .description(description.orElse(null))
                .apply { priority = this@Builder.priority }
    }

    companion object {
        const val EMPTY_PLACEHOLDER = "ROOT"

        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        fun emptyCommandBuilder() = ChainCommandBuilder(EMPTY_PLACEHOLDER)
    }
}