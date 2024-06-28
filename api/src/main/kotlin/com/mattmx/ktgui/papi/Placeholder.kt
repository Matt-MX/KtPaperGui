package com.mattmx.ktgui.papi

import com.mattmx.ktgui.commands.declarative.ChainCommandBuilder

class Placeholder(
    val match: ChainCommandBuilder,
    val supplier: (PlaceholderParseContext) -> Any?
) {
    var priority = 0

    fun parse(context: PlaceholderParseContext) = supplier.invoke(context)
}