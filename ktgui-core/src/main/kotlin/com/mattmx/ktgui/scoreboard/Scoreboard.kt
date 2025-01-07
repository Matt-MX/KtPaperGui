package com.mattmx.ktgui.scoreboard

import net.kyori.adventure.text.Component
import java.util.*

abstract class Scoreboard(
    initialTitle: Component
) {
    val viewers = mutableSetOf<UUID>()
    val content = ScoreboardContent()
    var title: Component = initialTitle
        set(value) {
            field = value
            updateTitle()
        }

    fun lines(block: ScoreboardContent.() -> Unit) = apply {
        content.apply(block)
    }

    abstract fun addViewer(uuid: UUID) : Boolean

    abstract fun removeViewer(uuid: UUID) : Boolean

    abstract fun update()

    abstract fun updateTitle()

}