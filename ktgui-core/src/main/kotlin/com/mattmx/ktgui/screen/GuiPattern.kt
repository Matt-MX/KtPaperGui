package com.mattmx.ktgui.screen

import com.mattmx.ktgui.button.GuiButton

class GuiPattern<B : GuiButton<*, *, *, *>>(
    val gui: GuiScreen<*, B>,
    val stringPattern: String
) {

    init {
        val split = stringPattern.split("\n")

        require(gui.guiType.rows != null) { "Can only apply gui patterns to row gui types." }
        require(gui.guiType.rows == split.size) { "Pattern rows must be same size as gui rows." }
        require(split.all { line -> line.length == Row.ROW_SIZE }) { "Pattern rows should all be ${Row.ROW_SIZE}" }
    }

    operator fun set(slot: Char, button: B) {
        gui.apply {
            button slots get(slot)
        }
    }

    operator fun get(slot: Char) = stringPattern
        .mapIndexed { slot, char -> slot to char }
        .filter { (_, char) -> slot == char }
        .map { (slot, _) -> slot }

    companion object {

        fun <B : GuiButton<*, *, *, *>> GuiScreen<*, B>.pattern(pattern: String): GuiPattern<B> {
            return GuiPattern(this, pattern.trimIndent())
        }

    }
}