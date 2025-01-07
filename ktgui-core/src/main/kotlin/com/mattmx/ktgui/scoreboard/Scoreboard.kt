package com.mattmx.ktgui.scoreboard

import com.mattmx.ktgui.tasks.TaskTracker
import net.kyori.adventure.text.Component
import java.util.*

abstract class Scoreboard(
    initialTitle: Component
) {
    lateinit var tasks: TaskTracker<*>
    val viewers = mutableSetOf<UUID>()
    val content by lazy { ScoreboardContent(this) }
    var title: Component = initialTitle
        set(value) {
            field = value
            updateTitle()
        }

    fun lines(block: ScoreboardContent.() -> Unit) = apply {
        content.apply(block)
    }

    abstract fun updating(plugin: Any) : Scoreboard

    abstract fun addViewer(uuid: UUID) : Boolean

    abstract fun removeViewer(uuid: UUID) : Boolean

    abstract fun update()

    abstract fun updateLine(index: Int)

    abstract fun updateTitle()

    fun dispose() {
        if (::tasks.isInitialized) {
            tasks.cancelAll()
        }
    }

}