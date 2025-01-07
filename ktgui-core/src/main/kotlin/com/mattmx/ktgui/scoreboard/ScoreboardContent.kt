package com.mattmx.ktgui.scoreboard

import net.kyori.adventure.text.Component

open class ScoreboardContent {
    val content = mutableListOf<Component>()

    operator fun Component.unaryPlus() {
        content.add(this)
    }

    operator fun set(line: Int, text: Component) {
        fillUntil(line)

        content.add(text)
    }

    fun fillUntil(line: Int) {
        while (content.size < line) {
            content.add(Component.empty())
        }
    }
}