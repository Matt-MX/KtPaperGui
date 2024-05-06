package com.mattmx.ktgui.creator

import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.utils.not

class GuiDesigner(
    val name: String
) : GuiScreen(!"Designer ($name&r)") {

    init {
        click.any {
            event.isCancelled = false

            // todo save instance
        }
    }

    fun export(): String {
        val start = "gui(\"$name\") {"
        val middle = items.values.filterIsInstance<GuiDesignerButton>()
            .joinToString("\n") { it.full + " childOf this" }
        val end = "}"

        return "$start$middle$end"
    }

}