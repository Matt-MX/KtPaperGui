package com.mattmx.ktgui.scoreboard

import net.kyori.adventure.text.Component

open class ScoreboardContent {
    val content = mutableListOf<Component>()

    operator fun Component.unaryPlus() {
        require(content.size <= MAX_ENTRIES)

        content.add(this)
    }

    operator fun set(line: Int, text: Component) {
        require(line < MAX_ENTRIES)

        fillUntil(line)

        content[line] = text
    }

    fun fillUntil(line: Int) {
        require(line < MAX_ENTRIES)

        while (content.size < line) {
            content.add(Component.empty())
        }
    }

    companion object {
        const val MAX_ENTRIES = 15
    }
}